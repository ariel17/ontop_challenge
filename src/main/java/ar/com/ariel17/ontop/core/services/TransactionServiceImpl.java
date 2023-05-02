package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApi;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.clients.WalletApi;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final int INSUFFICIENT_BALANCE = -1;

    private BankAccountRepository bankAccountRepository;

    private BankAccountOwner sourceOwner;

    private TransactionFactory transactionFactory;

    private LockService lockService;

    private WalletApi walletAPI;

    private PaymentProviderApi paymentProviderAPI;

    private PaymentRepository paymentRepository;

    private MovementRepository movementRepository;

    @Override
    public Transaction transfer(@NonNull Long userId, @NonNull BankAccountOwner recipient, @NonNull BigDecimal amount) throws TransactionException {

        try {
            bankAccountRepository.save(recipient);
        } catch (Exception e) {
            throw new TransactionException(e);
        }

        Transaction transaction = transactionFactory.createEgress(userId, sourceOwner, recipient, amount);
        BigDecimal total = transaction.total();
        Long walletTransactionId = null;

        try (LockRepository lockRepository = lockService.createLockForUserId(userId)) {
            boolean acquired = lockRepository.acquire();

            if (!acquired) {
                throw new TransactionException(String.format("Lock for user %d could not be acquired", userId));
            }

            if (walletAPI.getBalance(userId).compareTo(total) == INSUFFICIENT_BALANCE) {
                throw new TransactionException("Balance is insufficient to complete transaction");
            }

            walletTransactionId = walletAPI.createTransaction(userId, total.negate());
            transaction.setWalletTransactionId(walletTransactionId);

            Payment payment = null;
            try {
                payment = paymentProviderAPI.createPayment(sourceOwner, recipient, amount);
                if (payment.isError()) {
                    rollbackWalletTransaction(userId, total, walletTransactionId);
                    throw new TransactionException("Payment provider response is error");
                }
            } finally {
                paymentRepository.save(payment);
            }

            transaction.setPaymentId(payment.getId());
            transaction = movementRepository.save(transaction);

        } catch (PaymentProviderApiException e) {
            rollbackWalletTransaction(userId, total, walletTransactionId);
            throw new TransactionException(e);

        } catch (TransactionException e) {
            throw e;

        } catch (Exception e) {
            throw new TransactionException(e);
        }

        return transaction;
    }

    private void rollbackWalletTransaction(Long userId, BigDecimal total, Long originalWalletTransferId) throws TransactionException {
        Long transactionId;
        try {
            transactionId = walletAPI.createTransaction(userId, total);
        } catch (WalletApiException e) {
            throw new TransactionException(String.format("Failed to restore Wallet transaction ID=%d", originalWalletTransferId), e);
        }

        // TODO log transaction id
    }
}
