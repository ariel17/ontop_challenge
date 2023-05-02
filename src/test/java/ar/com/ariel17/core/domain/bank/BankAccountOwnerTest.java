package ar.com.ariel17.core.domain.bank;

import ar.com.ariel17.core.domain.ValidatorTest;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BankAccountOwnerTest extends ValidatorTest {

    private BankAccount account;

    @BeforeEach
    public void setUp() {
        super.setUp();
        account = new BankAccount(1234, 1234, Currency.getInstance("USD"));
    }

    @Test
    public void testInvalidValues() {
        BankAccountOwner owner = new BankAccountOwner(null, 0, null, "", "", null, null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(5, violations.size());
    }

    @Test
    public void testGetName_withLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234, account, "1234", "John", "Doe", null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(0, violations.size());
        assertEquals("John Doe", owner.getName());
    }

    @Test
    public void testGetName_withoutLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234, account, "1234", "John", "", null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(0, violations.size());
        assertEquals("John", owner.getName());
    }
}
