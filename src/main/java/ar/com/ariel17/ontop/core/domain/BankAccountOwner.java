package ar.com.ariel17.ontop.core.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Date;


/**
 * Recipient contains data about a bank account and owner.
 */
@Validated
@AllArgsConstructor
@Data
public class BankAccountOwner {

    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Bank account cannot be null")
    private BankAccount bankAccount;

    @NotNull(message = "ID number cannot be null")
    private String idNumber;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    private String lastName;

    private Date createdAt;

    public String getName() {
        return String.format("%s %s", firstName, lastName).strip();
    }
}