package ar.com.ariel17.core.domain;

/**
 * Type contains available movement types to represent a transaction.
 */
public enum Type {
    TRANSFER("transfer"),
    FEE("fee");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
