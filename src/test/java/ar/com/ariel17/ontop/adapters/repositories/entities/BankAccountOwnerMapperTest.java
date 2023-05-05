package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.BankAccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BankAccountOwnerMapperTest {

    private BankAccountOwner owner;

    @BeforeEach
    public void setUp() {
        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        owner = BankAccountOwner.builder().
                id(999L).
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                createdAt(new Date()).build();
    }

    @Test
    public void testBankAccountOwnerToBankAccountOwnerEntity() {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(owner);
        compare(owner, entity);
    }

    @Test
    public void testBankAccountOwnerEntityToBankAccountOwner() {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(owner);
        BankAccountOwner owner2 = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerEntityToBankAccountOwner(entity);
        compare(owner2, entity);
    }

    private void compare(BankAccountOwner owner, BankAccountOwnerEntity entity) {
        assertNotNull(owner.getId());
        assertNotNull(entity.getId());
        assertEquals(owner.getId(), entity.getId());

        assertNotNull(owner.getUserId());
        assertNotNull(entity.getUserId());
        assertEquals(owner.getUserId(), entity.getUserId());

        assertNotNull(owner.getBankAccount().getRouting());
        assertNotNull(entity.getRouting());
        assertEquals(owner.getBankAccount().getRouting(), entity.getRouting());

        assertNotNull(owner.getBankAccount().getAccount());
        assertNotNull(entity.getAccount());
        assertEquals(owner.getBankAccount().getAccount(), entity.getAccount());

        assertNotNull(owner.getBankAccount().getCurrency());
        assertNotNull(entity.getCurrency());
        assertEquals(owner.getBankAccount().getCurrency(), entity.getCurrency());

        assertNotNull(owner.getIdNumber());
        assertNotNull(entity.getIdNumber());
        assertEquals(owner.getIdNumber(), entity.getIdNumber());

        assertNotNull(owner.getFirstName());
        assertNotNull(entity.getFirstName());
        assertEquals(owner.getFirstName(), entity.getFirstName());

        assertNotNull(owner.getLastName());
        assertNotNull(entity.getLastName());
        assertEquals(owner.getLastName(), entity.getLastName());

        assertNotNull(owner.getCreatedAt());
        assertNotNull(entity.getCreatedAt());
        assertEquals(owner.getCreatedAt(), entity.getCreatedAt());
    }
}
