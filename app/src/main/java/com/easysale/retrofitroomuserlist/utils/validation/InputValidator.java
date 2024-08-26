package com.easysale.retrofitroomuserlist.utils.validation;

import android.util.Patterns;
import android.widget.EditText;

public class InputValidator {
    public static final String FIRST_NAME_REQUIRED = "First name is required!";
    public static final String LAST_NAME_REQUIRED = "Last name is required!";
    public static final String EMAIL_REQUIRED = "Email is required!";
    public static final String INVALID_EMAIL_FORMAT = "Invalid email format!";

    public static boolean validateUserInput(EditText firstNameEditText, EditText lastNameEditText, EditText emailEditText) {
        boolean isValid = true;

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameEditText.setError(FIRST_NAME_REQUIRED);
            firstNameEditText.requestFocus();
            isValid = false;
        }

        if (lastName.isEmpty()) {
            lastNameEditText.setError(LAST_NAME_REQUIRED);
            lastNameEditText.requestFocus();
            isValid = false;
        }

        if (email.isEmpty()) {
            emailEditText.setError(EMAIL_REQUIRED);
            emailEditText.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(INVALID_EMAIL_FORMAT);
            emailEditText.requestFocus();
            isValid = false;
        }

        return isValid;
    }
}
