package ar.com.ariel17.core.services.transaction;

import ar.com.ariel17.core.clients.payment.PaymentProviderApi;
import ar.com.ariel17.core.clients.payment.PaymentProviderApiException;
import ar.com.ariel17.core.clients.wallet.WalletApi;
import ar.com.ariel17.core.clients.wallet.WalletApiException;
import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.domain.transaction.Transaction;
import ar.com.ariel17.core.repositories.LockRepository;
import ar.com.ariel17.core.repositories.movement.MovementRepository;
import ar.com.ariel17.core.repositories.payment.PaymentRepository;
import ar.com.ariel17.core.repositories.recipient.RecipientRepository;
import ar.com.ariel17.core.repositories.recipient.RecipientRepositoryException;
import ar.com.ariel17.core.services.lock.LockService;
import lombok.NonNull;

import java.math.BigDecimal;

public class TransactionServiceImpl implements TransactionService {

    private static final int INSUFFICIENT_BALANCE = -1;

    private RecipientRepository recipientRepository;

    private BankAccountOwner sourceOwner;

    private TransactionFactory transactionFactory;

    private LockService lockService;

    private WalletApi walletAPI;

    private PaymentProviderApi paymentProviderAPI;

    private PaymentRepository paymentRepository;

    private MovementRepository movementRepository;

    public TransactionServiceImpl(
            @NonNull RecipientRepository recipientRepository,
            @NonNull BankAccountOwner sourceOwner,
            @NonNull TransactionFactory transactionFactory,
            @NonNull LockService lockService,
            @NonNull WalletApi walletAPI,
            @NonNull PaymentProviderApi paymentProviderAPI,
            @NonNull PaymentRepository paymentRepository,
            @NonNull MovementRepository movementRepository
    ) {
        this.recipientRepository = recipientRepository;
        this.sourceOwner = sourceOwner;
        this.transactionFactory = transactionFactory;
        this.lockService = lockService;
        this.walletAPI = walletAPI;
        this.paymentProviderAPI = paymentProviderAPI;
        this.paymentRepository = paymentRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public void transfer(@NonNull Long userId, BankAccountOwner recipient, @NonNull BigDecimal amount) throws TransactionException {

        try {
            recipientRepository.save(recipient);
        } catch (RecipientRepositoryException e) {
            throw new TransactionException(e);
        }

        Transaction transaction = transactionFactory.createEgress(userId, sourceOwner, recipient, amount);
        BigDecimal total = transaction.total();
        Long walletTransferId = null;

        try (LockRepository lockRepository = lockService.createLockForUserId(userId)) {
            boolean acquired = lockRepository.acquire();

            if (!acquired) {
                throw new TransactionException(String.format("Lock for user %d could not be acquired", userId));
            }

            if (walletAPI.getBalance(userId).compareTo(total) == INSUFFICIENT_BALANCE) {
                throw new TransactionException("Balance is insufficient to complete transaction");
            }

            walletTransferId = walletAPI.createTransaction(userId, total.negate());
            transaction.setWalletTransactionId(walletTransferId);

            Payment payment = paymentProviderAPI.createPayment(sourceOwner, recipient, amount);
            paymentRepository.save(payment);

            transaction.setPaymentId(payment.getId());
            movementRepository.save(transaction.getMovements());

        } catch (PaymentProviderApiException e) {
            Long transactionId;
            try {
                transactionId = walletAPI.createTransaction(userId, total);
            } catch (WalletApiException ex) {
                throw new TransactionException(String.format("Failed to restore Wallet transaction ID=%d", walletTransferId), e);
            }

            // TODO log transaction id
            throw new TransactionException(e);

        } catch (Exception e) {
            throw new TransactionException(e);
        }
    }
}
