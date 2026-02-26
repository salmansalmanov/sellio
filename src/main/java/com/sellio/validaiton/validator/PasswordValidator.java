package com.sellio.validaiton.validator;

import com.sellio.validation.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final List<Character> SYMBOLS = List.of('!', '@', '_');

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasSymbol = false;
        boolean hasNumber = false;

        if (password == null || password.length() < 8 ||
                password.length() > 20 || password.isBlank()) {
            return false;
        }

        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                hasUppercase = true;
            } else if (Character.isLowerCase(password.charAt(i))) {
                hasLowercase = true;
            } else if (Character.isDigit(password.charAt(i))) {
                hasNumber = true;
            } else if (SYMBOLS.contains(password.charAt(i))) {
                hasSymbol = true;
            }
        }
        return hasUppercase && hasLowercase && hasSymbol && hasNumber;
    }
}