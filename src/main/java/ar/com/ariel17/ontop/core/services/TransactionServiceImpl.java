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

    private BankAccountOwner onTopAccount;

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
            recipient = bankAccountRepository.getByIdAndUserId(recipient.getId(), userId);
            logger.info("Bank account fetched: user_id={}, owner_id={}", userId, recipient.getId());
        }

        // -amount because it is a withdrawal
        Transaction transaction = transactionFactory.createWithdraw(userId, onTopAccount, recipient, amount.negate());

        // total is NEGATIVE
        BigDecimal total = transaction.total();
        Long walletTransactionId = null;

        try (LockRepository lockRepository = context.getBean(LockRepository.class, userId)) {
            if (!lockRepository.acquire()) {
                String message = String.format("Lock for user_id=%d could not be acquired", userId);
                logger.error(message);
                throw new TransactionException(message);
            }

            BigDecimal balance = walletAPIClient.getBalance(userId);
            logger.info("Wallet balance for user_id={}: {}", userId, balance);

            // |total| to compare balance
            if (balance.compareTo(total.abs()) == INSUFFICIENT_BALANCE) {
                logger.info("Transaction not completed due to insufficient balance: user_id={}, balance={}, total={}", userId, balance, total.abs());
                throw new InsufficientBalanceException("Balance is insufficient to complete transaction");
            }

            // NEGATIVE (as is) to subtract from balance
            walletTransactionId = walletAPIClient.createTransaction(userId, total);
            logger.info("Withdraw from wallet OK: user_id={}, total={}, transaction_id={}", userId, total, walletTransactionId);

            transaction.setWalletTransactionId(walletTransactionId);

            logger.info("Requesting transfer between accounts to provider: user_id={}, amount={}", userId, amount);
            Payment payment = null;
            try {
                // amount is POSITIVE
                payment = paymentProviderAPIClient.createPayment(onTopAccount, recipient, amount);

            } catch (PaymentProviderApiException e) {
                logger.error("Transfer from provider FAILED: user_id={}", userId, e);

                // |total| to restore balance
                Long revertWalletTransactionId = walletAPIClient.createTransaction(userId, total.abs());
                logger.info("Withdraw from wallet REVERTED: user_id={}, total={}, transaction_id={}", userId, total.abs(), revertWalletTransactionId);

                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
                return movementRepository.save(transaction);

            } finally {
                payment = paymentRepository.save(payment);
            }

            logger.info("Transfer from provider completed: user_id={}, payment={}", userId, payment);

            if (payment.isError()) {
                // |total| to restore balance
                Long revertWalletTransactionId = walletAPIClient.createTransaction(userId, total.abs());
                logger.info("Withdraw from wallet REVERTED: user_id={}, total={}, transaction_id={}", userId, total.abs(), revertWalletTransactionId);

                transactionFactory.revertOperation(transaction, Operation.WITHDRAW, revertWalletTransactionId);
            }

            transaction.setPayment(payment);
            transaction = movementRepository.save(transaction);

        } catch (UserNotFoundException | TransactionException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            throw new TransactionException(e);
        }

        return transaction;
    }
}
