package ar.com.ariel17.ontop.adapters.clients.entities.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Wallet API response body when successful.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletTransactionResponse {

    @JsonProperty("wallet_transaction_id")
    private Long walletTransactionId;

    private BigDecimal amount;

    @JsonProperty("user_id")
    private Long userId;
}
