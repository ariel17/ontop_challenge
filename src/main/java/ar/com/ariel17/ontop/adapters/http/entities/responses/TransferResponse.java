package ar.com.ariel17.ontop.adapters.http.entities.responses;

import ar.com.ariel17.ontop.core.domain.Operation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String status;

    private Operation operation;

    @JsonProperty("recipient_id")
    private Long recipientId;

    private List<TransferMovement> movements;
}
