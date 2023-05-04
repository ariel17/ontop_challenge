package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApiClient;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Payment;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import ar.com.ariel17.ontop.core.repositories.LockRepository;
import ar.com.ariel17.ontop.core.repositories.MovementRepository;
import ar.com.ariel17.ontop.core.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final int INSUFFICIENT_BALANCE = -1;

    @Autowired
    private ApplicationContext context;

    private BankAccountRepository bankAccountRepository;

    private BankAccountOwner sourceOwner;

    private TransactionFactory transactionFactory;

    private WalletApiClient walletAPIClient;

    private PaymentProviderApiClient paymentProviderAPIClient;

    private PaymentRepository paymentRepository;

    private MovementRepository movementRepository;

    @Override
    public Transaction transfer(@NonNull Long userId, @NonNull BankAccountOwner recipient, @NonNull BigDecimal amount) throws TransactionException {

        try {
            bankAccountRepository.save(recipient);
        } catch (Exception e) {
            throw new TransactionException(e);
        }

        Transaction transaction = transactionFactory.createWithdraw(userId, sourceOwner, recipient, amount);
        BigDecimal total = transaction.total();
        Long walletTransactionId = null;

        try (LockRepository lockRepository = context.getBean(LockRepository.class, userId)) {
            if (!lockRepository.acquire()) {
                throw new TransactionException(String.format("Lock for user %d could not be acquired", userId));
            }

            if (walletAPIClient.getBalance(userId).compareTo(total) == INSUFFICIENT_BALANCE) {
                throw new TransactionException("Balance is insufficient to complete transaction");
            }

            walletTransactionId = walletAPIClient.createTransaction(userId, total.negate());
            transaction.setWalletTransactionId(walletTransactionId);

            Payment payment = null;
            try {
                payment = paymentProviderAPIClient.createPayment(sourceOwner, recipient, amount);

            } catch (PaymentProviderApiException e) {
                Long revertWalletTransactionId = rollbackWalletTransaction(userId, total, walletTransactionId);
                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
                return movementRepository.save(transaction);

            } finally {
                paymentRepository.save(payment);
            }

            if (payment.isError()) {
                Long revertWalletTransactionId = rollbackWalletTransaction(userId, total, walletTransactionId);
                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
            }

            transaction.setPaymentId(payment.getId());
            transaction = movementRepository.save(transaction);

        } catch (TransactionException e) {
            throw e;

        } catch (Exception e) {
            throw new TransactionException("Unexpected exception", e);
        }

        return transaction;
    }

    private Long rollbackWalletTransaction(Long userId, BigDecimal total, Long originalWalletTransferId) throws TransactionException {
        try {
            return walletAPIClient.createTransaction(userId, total);
        } catch (WalletApiException e) {
            throw new TransactionException(String.format("Failed to restore Wallet transaction ID=%d", originalWalletTransferId), e);
        }
    }
}
