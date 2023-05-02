package ar.com.ariel17.core.domain.transaction;

import ar.com.ariel17.core.domain.ValidatorTest;
import ar.com.ariel17.core.domain.bank.BankAccount;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovementTest extends ValidatorTest {

    private Currency currency;

    private BigDecimal amount;

    private BankAccount account1;

    private BankAccount account2;

    private Integer walletTransactionId;

    private UUID paymentId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        currency = Currency.getInstance("USD");
        amount = new BigDecimal(1234);
        account1 = new BankAccount(1234, 1234, currency);
        account2 = new BankAccount(4321, 4321, currency);
        walletTransactionId = 1234;
        paymentId = UUID.randomUUID();
    }

    @Test
    public void testInvalidValues_null() {
        Set<ConstraintViolation<Movement>> violations = validator.validate(
                new Movement(null, null, null, null, null, null, null, null, null, null, null)
        );
        assertEquals(5, violations.size());
    }

    @Test
    public void testInvalidValues_zero() {
        Set<ConstraintViolation<Movement>> violations = validator.validate(
                new Movement(null, null, null, null, null, new BigDecimal(0), null, null, null, null, null)
        );
        assertEquals(5, violations.size());
    }

    @Test
    public void testIsValid_validFee() {
        Movement m = new Movement(null, 1234, Type.FEE, Operation.EGRESS, currency, amount, null, null, null, null, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(0, violations.size());
    }

    @Test
    public void testIsValid_invalidFeeWithAccounts() {
        Movement m = new Movement(null, 1234, Type.FEE, Operation.EGRESS, currency, amount, account1, account2, null, null, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(1, violations.size());
    }

    @Test
    public void testIsValid_validNotFee() {
        Movement m = new Movement(null, 1234, Type.TRANSFER, Operation.EGRESS, currency, amount, account1, account2, null, null, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(0, violations.size());
    }

    @Test
    public void testIsValid_sameAccounts() {
        Movement m = new Movement(null, 1234, Type.TRANSFER, Operation.EGRESS, currency, amount, account1, account1, null, null, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(1, violations.size());
    }

    @Test
    public void testIsComplete_complete() {
        Movement m = new Movement(null, 1234, Type.TRANSFER, Operation.EGRESS, currency, amount, account1, account2, walletTransactionId, paymentId, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(0, violations.size());
        assertTrue(m.isComplete());
    }

    @Test
    public void testIsComplete_incomplete() {
        Movement m = new Movement(null, 1234, Type.TRANSFER, Operation.EGRESS, currency, amount, account1, account2, null, null, null);
        Set<ConstraintViolation<Movement>> violations = validator.validate(m);
        assertEquals(0, violations.size());
        assertFalse(m.isComplete());
    }

    @Test
    public void testSetPaymentId_nonFee() {
        Movement m = new Movement(null, 1234, Type.TRANSFER, Operation.EGRESS, currency, amount, account1, account2, null, null, null);
        m.setPaymentId(paymentId);
    }

    @Test
    public void testSetPaymentId_fee() {
        Movement m = new Movement(null, 1234, Type.FEE, Operation.EGRESS, currency, amount, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> m.setPaymentId(paymentId));
    }
}
