package com.example.mapsgps;

import com.google.android.material.textfield.TextInputLayout;

public class Credentials {

    public static String getData(TextInputLayout field){
        return field.getEditText().getText().toString();
    }

    public static boolean checkEmpty(TextInputLayout field) {
        if (getData(field).isEmpty()){
            field.setError("Field cannot be empty");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    public static boolean checkEmailEmpty(TextInputLayout field) {
        String data = getData(field);
        if (data.isEmpty() || data.trim().isEmpty()){
            field.setError("Field cannot be empty");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    private static boolean checkLength(TextInputLayout field) {
        if (getData(field).length() < 8){
            field.setError("Length should be at least 8 symbols");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    private static boolean checkSymbols(TextInputLayout field) {
        String pattern = "[a-zA-z0-9]+";
        if (!getData(field).matches(pattern)){
            field.setError("Password can consist only a-z, A-Z, 0-9");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    public static boolean validatePassword(TextInputLayout passField){
        return checkEmpty(passField) && checkSymbols(passField) && checkLength(passField);
    }

}
