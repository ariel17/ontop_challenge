package ar.com.ariel17.app.core.domain;


import jakarta.validation.Validation;
import jakarta.validation.Validator;

public abstract class ValidatorTest {

    protected Validator validator;

    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
