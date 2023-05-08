package ar.com.ariel17.ontop.core.domain;

/**
 * Bank account types availables for external payment providers as source
 * account.
 */
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
