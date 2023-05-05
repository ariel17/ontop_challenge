package ar.com.ariel17.ontop.core.domain;

public enum BankAccountType {

    COMPANY("COMPANY");

    private final String type;

    BankAccountType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
