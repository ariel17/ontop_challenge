package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentTest {

    @Test
    public void testIsError_notError() {
        Payment p = new Payment(null, null, PaymentStatus.PROCESSING, null, null);
        assertFalse(p.isError());
    }

    @Test
    public void testIsError_error() {
        Payment p = new Payment(null, null, PaymentStatus.FAILED, "oh my god what we've done", null);
        assertTrue(p.isError());
    }
}
