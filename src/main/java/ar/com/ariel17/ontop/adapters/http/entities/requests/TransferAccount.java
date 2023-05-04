package ar.com.ariel17.ontop.adapters.http.entities.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public class TransferAccount {

    @JsonProperty("routing_number")
    private Long routingNumber;

    @JsonProperty("account_number")
    @NotNull(message = "Account number cannot be null")
    private Long accountNumber;

    private Currency currency;
}
