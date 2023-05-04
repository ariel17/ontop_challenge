package ar.com.ariel17.ontop.adapters.http.entities.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Validated
public class TransferAccount {

    @JsonProperty("routing_number")
    @NotNull(message = "Routing number cannot be null")
    @Size(min = 9, max = 9, message = "Routing number is 9 digit string")
    private String routingNumber;

    @JsonProperty("account_number")
    @NotNull(message = "Account number cannot be null")
    @Size(min = 9, max = 9, message = "Account number is 9 digit string")
    private String accountNumber;

    private Currency currency;
}
