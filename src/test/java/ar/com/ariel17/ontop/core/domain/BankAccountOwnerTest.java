package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BankAccountOwnerTest {

    private BankAccount account;

    @BeforeEach
    public void setUp() {
        account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
    }

    @Test
    public void testGetName_withLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "Doe", null);
        assertEquals("John Doe", owner.getName());
    }

    @Test
    public void testGetName_withoutLastName() {
        BankAccountOwner owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "", null);
        assertEquals("John", owner.getName());
    }
}
