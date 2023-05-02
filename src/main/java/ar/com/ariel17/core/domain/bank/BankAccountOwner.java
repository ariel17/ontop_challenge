package ar.com.ariel17.core.domain.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @Positive(message = "User ID has to be positive")
    private Long userId;

    @NotNull(message = "Bank account cannot be null")
    private BankAccount bankAccount;

    @NotBlank(message = "ID number cannot be blank")
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