package ar.com.ariel17.core.domain.bank;

import ar.com.ariel17.core.domain.BaseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.Date;


/**
 * Recipient contains data about a bank account and owner.
 */
@Validated
public class BankAccountOwner extends BaseModel<Integer> {

    @Positive(message = "User ID has to be positive")
    private final Integer userId;

    @NotNull(message = "Bank account cannot be null")
    private final BankAccount bankAccount;

    @NotBlank(message = "ID number cannot be blank")
    private final String idNumber;

    @NotBlank(message = "First name cannot be blank")
    private final String firstName;

    @NotNull(message = "Last name cannot be null")
    private final String lastName;

    /**
     * Creates a new bank account owner.
     *
     * @param id          The associated ID in database. If null, the recipient was not yet stored.
     * @param userId      The user ID associated to this recipient.
     * @param bankAccount The bank account identification data.
     * @param idNumber    The bank account's owner ID number.
     * @param firstName   The bank account's first name.
     * @param lastName    The bank account's last name.
     * @param createdAt   The creation date. If null, the recipient was not yet stored.
     */
    public BankAccountOwner(Integer id, Integer userId, BankAccount bankAccount, String idNumber, String firstName, String lastName, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.bankAccount = bankAccount;
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName).strip();
    }
}