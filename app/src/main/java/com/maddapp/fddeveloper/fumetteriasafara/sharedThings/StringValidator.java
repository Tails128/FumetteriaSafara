package com.maddapp.fddeveloper.fumetteriasafara.sharedThings;

/**
 * a simple collection of string validation methods
 */
public class StringValidator {

    /**
     * checks if a mail has a valid format
     * @param email the string to check
     * @return
     */
    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * checks if the password is valid for the FireBase auth
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password) {
        return password.length() > 7;
    }
}
