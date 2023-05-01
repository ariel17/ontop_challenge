package ar.com.ariel17.app.core.domain.bank;

import ar.com.ariel17.core.domain.bank.BankAccount;
import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BankAccountOwnerTest {

    private static Validator validator;

    private static BankAccount account;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
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
