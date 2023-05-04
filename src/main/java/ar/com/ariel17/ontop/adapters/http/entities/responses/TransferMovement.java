package ar.com.ariel17.ontop.adapters.http.entities.responses;

import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Type;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferMovement {

    private Long id;

    private Type type;

    private Operation operation;

    private Currency currency;

    private BigDecimal amount;

    @JsonProperty("recipient_id")
    private Long recipientId;

    @JsonProperty("created_at")
    private Date createdAt;
}
