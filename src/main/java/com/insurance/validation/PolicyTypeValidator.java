package com.insurance.validation;

import com.insurance.domain.ProductType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PolicyTypeValidator implements ConstraintValidator<PolicyType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            ProductType.valueOf(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
