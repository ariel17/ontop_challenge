package ar.com.ariel17.app.core.domain.bank;

import ar.com.ariel17.core.domain.bank.BankAccount;
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
public class BankAccountTest {

    private BankAccount bankAccount;

    private Currency currency = Currency.getInstance("USD");

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testNullValues() {
        bankAccount = new BankAccount(1234, 1234, null);
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(bankAccount);
        assertEquals(1, violations.size());
    }

    @Test
    public void testNegativeValues() {
        bankAccount = new BankAccount(-1234, -1234, currency);
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(bankAccount);
        assertEquals(2, violations.size());
    }
}
