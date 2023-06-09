package ar.com.ariel17.ontop.adapters.http.entities.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * The REST API request body required. It contains the recipient data or id and
 * details about the amount of money to transfer.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public class TransferRequest {

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @JsonProperty("recipient_id")
    @Positive(message = "Recipient ID has to be positive")
    private Long recipientId;

    @Valid
    private TransferRecipient recipient;

    private Currency currency;

    @Positive(message = "Amont has to be positive")
    private BigDecimal amount;
}
