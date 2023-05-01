package ar.com.ariel17.app.core.domain.bank;

import ar.com.ariel17.app.core.domain.ValidatorTest;
import ar.com.ariel17.core.domain.bank.BankAccount;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BankAccountTest extends ValidatorTest {

    @Test
    public void testInvalidValues() {
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(
                new BankAccount(0, 0, null)
        );
        assertEquals(3, violations.size());
    }
}
