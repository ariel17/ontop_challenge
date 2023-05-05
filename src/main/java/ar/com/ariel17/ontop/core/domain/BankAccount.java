package ar.com.ariel17.ontop.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Currency;

/**
 * BankAccount represents the minimal data required to identify a bank account.
 */
@AllArgsConstructor
@Builder
@Data
public class BankAccount {

    private String routing;

    private String account;

    private Currency currency;

    private String type;
}