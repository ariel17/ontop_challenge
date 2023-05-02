package ar.com.ariel17.core.domain;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class PaymentTest extends ValidatorTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testInvalidValues() {
        Set<ConstraintViolation<Payment>> violations = validator.validate(
                new Payment(null, null, null, null, null)
        );
        assertEquals(3, violations.size());
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
