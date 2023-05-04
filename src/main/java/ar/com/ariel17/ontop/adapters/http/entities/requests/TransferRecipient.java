package ar.com.ariel17.ontop.adapters.http.entities.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
public class TransferRecipient {

    @NotNull(message = "Account cannot be null")
    private TransferAccount account;

    @JsonProperty("id_number")
    @NotNull(message = "ID number cannot be null")
    private String idNumber;

    @JsonProperty("first_name")
    @NotNull(message = "First name cannot be null")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;
}
