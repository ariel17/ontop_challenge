package ar.com.ariel17.ontop.core.domain;

/**
 * Type contains available movement types to represent a transaction.
 */
public enum MovementType {
    TRANSFER("transfer"),
    FEE("fee");

    private final String type;

    MovementType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
