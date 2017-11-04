package com.maddapp.fddeveloper.fumetteriasafara.sharedThings;

/**
 * a simple collection of string validation methods
 */
public class StringValidator {

    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 7;
    }
}
