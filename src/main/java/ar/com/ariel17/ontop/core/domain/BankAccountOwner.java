package ar.com.ariel17.ontop.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * Recipient contains data about a bank account and owner.
 */
@AllArgsConstructor
@Builder
@Data
public class BankAccountOwner implements Serializable {

    private Long id;

    private Long userId;

    private BankAccount bankAccount;

    private String idNumber;

    private String firstName;

    private String lastName;

    private Date createdAt;

    public String getName() {
        if (lastName == null) {
            return firstName.strip();
        }
        return String.format("%s %s", firstName, lastName).strip();
    }
}