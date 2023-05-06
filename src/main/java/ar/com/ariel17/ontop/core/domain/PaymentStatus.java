package ar.com.ariel17.ontop.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {

    @JsonProperty("Processing")
    PROCESSING("Processing"),

    @JsonProperty("Failed")
    FAILED("Failed");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
