package com.mas.jacketcoach.helper;

import android.text.TextUtils;
import android.util.Patterns;

public class Validator {
    // Helper method for validating an email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Helper method for validating a password
    // TODO: Add more logic
    // NOTE: Firebase Auth already performs some weak password checks
    public static boolean isValidPassword(String target) {
        return !(target.trim().equalsIgnoreCase(""));
    }

    // TODO: Add more logic
    public static boolean isValidText(String target) {
        return !(target.trim().equalsIgnoreCase(""));
    }
}
