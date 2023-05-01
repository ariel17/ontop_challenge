package ar.com.ariel17.core.domain.transaction;

/**
 * Type contains available movement types to represent a transaction.
 */
public enum Type {
    TRANSFER("transfer"),
    FEE("fee");

    private final String type;

    private Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
