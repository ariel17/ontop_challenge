package ar.com.ariel17.core.domain.transaction;

/**
 * Operation represents overall transaction intention.
 */
public enum Operation {
    EGRESS("egress");

    private final String name;

    private Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
