package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private Movement m1;

    private Movement m2;

    private Long walletTransactionId;

    private Payment payment;

    private Transaction t;

    @BeforeEach
    public void setUp() {
        Currency currency = Currency.getInstance("USD");
        BigDecimal amount = new BigDecimal(1234);

        BankAccount account1 = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(currency).build();
        BankAccountOwner onTopAccount = BankAccountOwner.builder().
                userId(0L).
                bankAccount(account1).
                firstName("ONTOP INC").
                build();

        BankAccount account2 = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                currency(currency).build();
        BankAccountOwner externalAccount = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account2).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        m1 = Movement.builder().
                userId(4321L).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).build();

        m2 = Movement.builder().
                userId(4321L).
                type(MovementType.FEE).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).build();

        walletTransactionId = 1234L;

        payment = Payment.builder().
                id(UUID.randomUUID()).
                status("ok").build();

        t = new Transaction();
    }

    @Test
    public void testAddMovement_valid() {
        t.addMovement(m1);
        t.addMovement(m2);
        assertEquals(2, t.getMovements().size());
    }

    @Test
    public void testTotal() {
        assertEquals(new BigDecimal(0), t.total());
        t.addMovement(m1);
        assertEquals(new BigDecimal(1234), t.total());
        t.addMovement(m2);
        assertEquals(new BigDecimal(2468), t.total());
    }

    @Test
    public void testSetWalletTransactionId_valid() {
        t.addMovement(m1);
        t.addMovement(m2);
        t.setWalletTransactionId(walletTransactionId);
        t.getMovements().forEach(m -> assertEquals(walletTransactionId, m.getWalletTransactionId()));
    }
}
