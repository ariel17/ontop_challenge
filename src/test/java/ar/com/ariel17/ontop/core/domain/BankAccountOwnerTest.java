package ar.com.ariel17.ontop.core.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BankAccountOwnerTest {

    private BankAccount account;

    @BeforeEach
    public void setUp() {
        account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
    }

    @Test
    public void testGetName_withLastName() {
        BankAccountOwner owner = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Doe").build();
        assertEquals("John Doe", owner.getName());
    }

    @Test
    public void testGetName_withoutLastName() {
        BankAccountOwner owner = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").build();
        assertEquals("John", owner.getName());
    }
}
