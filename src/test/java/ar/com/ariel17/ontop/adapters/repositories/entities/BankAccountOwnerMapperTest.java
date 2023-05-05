package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAccountOwnerMapperTest {

    private BankAccountOwner owner;

    @BeforeEach
    public void setUp() {
        BankAccount account = new BankAccount("0123456789", "012345678", Currency.getInstance("USD"));
        owner = new BankAccountOwner(10L, 99L, account, "123ABC", "John", "Doe", new Date());
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
        // TODO assert not null, not only equals
        assertEquals(owner.getId(), entity.getId());
        assertEquals(owner.getBankAccount().getRouting(), entity.getRouting());
        assertEquals(owner.getBankAccount().getAccount(), entity.getAccount());
        assertEquals(owner.getBankAccount().getCurrency(), entity.getCurrency());
        assertEquals(owner.getFirstName(), entity.getFirstName());
        assertEquals(owner.getLastName(), entity.getLastName());
        assertEquals(owner.getCreatedAt(), entity.getCreatedAt());
    }
}
