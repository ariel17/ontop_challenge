package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovementTest {

    private Currency currency;

    private BigDecimal amount;

    private BankAccountOwner owner1;

    private BankAccountOwner owner2;

    private Long walletTransactionId;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        currency = Currency.getInstance("USD");
        amount = new BigDecimal(1234);

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(currency).build();
        owner1 = BankAccountOwner.builder().
                userId(0L).
                bankAccount(account).
                firstName("ONTOP INC").build();

        account = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                currency(currency).build();
        owner2 = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        walletTransactionId = 1234L;
        payment = Payment.builder().
                id(UUID.randomUUID()).
                status("ok").build();
    }

    @Test
    public void testIsComplete_complete() {
        Movement m = Movement.builder().
                userId(4321L).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(owner1).
                externalAccount(owner2).
                walletTransactionId(walletTransactionId).
                payment(payment).build();
        assertTrue(m.isComplete());
    }

    @Test
    public void testIsComplete_incomplete() {
        Movement m = Movement.builder().
                userId(4321L).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(owner1).
                externalAccount(owner2).build();
        assertFalse(m.isComplete());
    }
}
