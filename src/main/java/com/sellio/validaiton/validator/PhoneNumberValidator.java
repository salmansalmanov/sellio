package com.sellio.validaiton.validator;

import com.sellio.validaiton.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (phoneNumber == null) {
            return false;
        }

        if (phoneNumber.length() != 13 ||
                !phoneNumber.startsWith("+")) {
            return false;
        }

        for (int i = 1; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}