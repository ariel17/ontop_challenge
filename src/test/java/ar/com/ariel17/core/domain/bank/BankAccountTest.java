package ar.com.ariel17.core.domain.bank;

import ar.com.ariel17.core.domain.ValidatorTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BankAccountTest extends ValidatorTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testInvalidValues() {
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(
                new BankAccount(0, 0, null)
        );
        assertEquals(3, violations.size());
    }
}
