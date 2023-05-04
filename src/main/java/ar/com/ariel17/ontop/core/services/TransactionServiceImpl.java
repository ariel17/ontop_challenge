package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApiClient;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Payment;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final int INSUFFICIENT_BALANCE = -1;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

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
    public Transaction transfer(
            Long userId, BankAccountOwner recipient, BigDecimal amount
    ) throws BankAccountOwnerNotFoundException, UserNotFoundException, TransactionException {

        if (recipient.getId() == null) {
            try {
                recipient = bankAccountRepository.save(recipient);
                logger.info("New bank account saved: user_id={}, owner_id={}", userId, recipient.getId());

            } catch (Exception e) {
                logger.error("Failed to save bank account owner", e);
                throw new TransactionException(e);
            }

        } else {
            recipient = bankAccountRepository.getById(recipient.getId());
            logger.info("Bank account fetched: user_id={}, owner_id={}", userId, recipient.getId());
        }

        Transaction transaction = transactionFactory.createWithdraw(userId, sourceOwner, recipient, amount);
        BigDecimal total = transaction.total();
        Long walletTransactionId = null;

        try (LockRepository lockRepository = context.getBean(LockRepository.class, userId)) {
            if (!lockRepository.acquire()) {
                String message = String.format("Lock for user_id=%d could not be acquired", userId);
                logger.error(message);
                throw new TransactionException(message);
            }

            if (walletAPIClient.getBalance(userId).compareTo(total) == INSUFFICIENT_BALANCE) {
                logger.info("Transaction not completed due to insufficient balance: user_id={}", userId);
                throw new TransactionException("Balance is insufficient to complete transaction");
            }

            walletTransactionId = walletAPIClient.createTransaction(userId, total.negate());
            logger.info("Withdraw from wallet OK: user_id={}, transaction_id={}", userId, walletTransactionId);

            transaction.setWalletTransactionId(walletTransactionId);

            Payment payment = null;
            try {
                payment = paymentProviderAPIClient.createPayment(sourceOwner, recipient, amount);

            } catch (PaymentProviderApiException e) {
                logger.error("Transfer from provider FAILED: user_id={}", userId, e);

                Long revertWalletTransactionId = walletAPIClient.createTransaction(userId, total);
                logger.info("Withdraw from wallet REVERTED: user_id={}, transaction_id={}", userId, revertWalletTransactionId);

                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
                return movementRepository.save(transaction);

            } finally {
                paymentRepository.save(payment);
            }

            logger.info("Transfer from provider completed: user_id={}, payment_id={}, payment_status={}", userId, payment.getId(), payment.getStatus());

            if (payment.isError()) {
                Long revertWalletTransactionId = walletAPIClient.createTransaction(userId, total);
                logger.info("Withdraw from wallet REVERTED: user_id={}, transaction_id={}", userId, revertWalletTransactionId);

                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
            }

            transaction.setPayment(payment);
            transaction = movementRepository.save(transaction);

        } catch (UserNotFoundException e) {
            logger.error("User does not exist", e);
            throw e;

        } catch (TransactionException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            throw new TransactionException("Unexpected exception", e);
        }

        return transaction;
    }
}
