package com.example.mapsgps.registration;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Class has functions to check correctness of credentials
 */
public class Credentials {

    /**
     * @param field Field to get the data from
     * @return String Value from field
     */
    public static String getData(TextInputLayout field){
        return field.getEditText().getText().toString();
    }

    /**
     * Check if field is not empty
     * If field is empty, set error on field
     * @param field Field for checking
     * @return boolean Has field not-null value
     */
    public static boolean checkEmpty(TextInputLayout field) {
        if (getData(field).isEmpty()){
            field.setError("Field cannot be empty");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    /**
     * Check if email field is not empty
     * If field is empty, set error on field
     * If field has only spaces, it is considered empty
     * @param field Field for checking
     * @return boolean Has field not-null value
     */
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

    /**
     * Check if value from field not short than minimum length is
     * If false, set error on field
     * @param field Field for checking
     * @return boolean is value >= min_length
     */
    private static boolean checkLength(TextInputLayout field, int min_length) {
        if (getData(field).length() < min_length){
            field.setError("Length should be at least " + min_length + " symbols");
            field.setErrorEnabled(true);
            return false;
        }
        field.setErrorEnabled(false);
        return true;
    }

    /**
     * Check if value from field has only allowed symbols
     * If false, set error on field
     * @param field Field for checking
     * @return boolean Is value has only allowed symbols
     */
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

    /**
     * Check if the password meets all requirements
     * @param passField Password field for checking
     * @return boolean Is password correct
     */
    public static boolean validatePassword(TextInputLayout passField){
        return checkEmpty(passField) && checkSymbols(passField) && checkLength(passField, 8);
    }

}
