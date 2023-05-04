package ar.com.ariel17.ontop.core.domain;

/**
 * Operation represents overall transaction intention.
 */
public enum Operation {
    WITHDRAW("withdraw"),
    TOP_UP("top_up"),
    REVERT("revert");

    private final String name;

    Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
