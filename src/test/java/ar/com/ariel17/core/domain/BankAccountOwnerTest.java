package ar.com.ariel17.core.domain;

import ar.com.ariel17.core.domain.BankAccount;
import ar.com.ariel17.core.domain.BankAccountOwner;
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
        account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
    }

    @Test
    public void testInvalidValues() {
        BankAccountOwner owner = new BankAccountOwner(null, 0L, null, "", "", null, null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(3, violations.size());
    }

    @Test
    public void testGetName_withLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "Doe", null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(0, violations.size());
        assertEquals("John Doe", owner.getName());
    }

    @Test
    public void testGetName_withoutLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "", null);
        Set<ConstraintViolation<BankAccountOwner>> violations = validator.validate(owner);
        assertEquals(0, violations.size());
        assertEquals("John", owner.getName());
    }
}
