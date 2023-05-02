package ar.com.ariel17.ontop.core.domain;

/**
 * Operation represents overall transaction intention.
 */
public enum Operation {
    EGRESS("egress");

    private final String name;

    Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
