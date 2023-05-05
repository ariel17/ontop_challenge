package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        BankAccount account1 = new BankAccount("0123456789", "012345678", currency);
        BankAccount account2 = new BankAccount("9876543210", "876543210", currency);

        m1 = new Movement(null, 4321L, Type.TRANSFER, Operation.WITHDRAW, currency, amount, account1, account2, null, null, null);
        m2 = new Movement(null, 4321L, Type.FEE, Operation.WITHDRAW, currency, amount, null, null, null, null, null);

        walletTransactionId = 1234L;
        payment = Payment.builder().id(UUID.randomUUID()).build();
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
    public void testSetPaymentId_valid() {
        t.addMovement(m1);
        t.addMovement(m2);
        t.setPayment(payment);
        t.getMovements().forEach(m -> {
            if (m.getType() == Type.FEE) {
                assertNull(m.getPaymentId());
            } else {
                assertEquals(payment.getId(), m.getPaymentId());
            }
        });
    }

    @Test
    public void testSetWalletTransactionId_valid() {
        t.addMovement(m1);
        t.addMovement(m2);
        t.setWalletTransactionId(walletTransactionId);
        t.getMovements().forEach(m -> assertEquals(walletTransactionId, m.getWalletTransactionId()));
    }
}
