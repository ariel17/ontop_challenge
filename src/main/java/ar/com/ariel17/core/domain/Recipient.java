package ar.com.ariel17.core.domain;

import java.util.Date;

/**
 * Recipient contains data about a bank account and owner.
 */
public class Recipient extends BaseModel<Integer> {

    private Integer userId;

    private BankAccount bankAccount;

    private String idNumber;

    private String firstName;

    private String lastName;

    /**
     * Creates a new recipient.
     *
     * @param id The associated ID in database. If null, the recipient was not yet stored.
     * @param userId The user ID associated to this recipient.
     * @param bankAccount The bank account identification data.
     * @param idNumber The bank account's owner ID number.
     * @param firstName The bank account's first name.
     * @param lastName The bank account's last name.
     * @param createdAt The creation date. If null, the recipient was not yet stored.
     */
    public Recipient(Integer id, Integer userId, BankAccount bankAccount, String idNumber, String firstName, String lastName, Date createdAt) {
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}