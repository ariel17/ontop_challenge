package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class PaymentTest {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testIsError_notError() {
        Payment p = new Payment(null, null, "ok", null, null);
        assertFalse(p.isError());
    }

    @Test
    public void testIsError_error() {
        Payment p = new Payment(null, null, "whatever", "oh my god what we've done", null);
        assertTrue(p.isError());
    }
}
