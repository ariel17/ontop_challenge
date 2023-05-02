package ar.com.ariel17.core.domain.bank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;

/**
 * BankAccount represents the minimal data required to identify a bank account.
 */
@Validated
@AllArgsConstructor
@Data
public class BankAccount {

    @Positive(message = "Routing number has to be positive")
    private Long routing;

    @Positive(message = "Account number has to be positive")
    private Long account;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;
}