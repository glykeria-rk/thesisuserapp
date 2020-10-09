package com.example.lockuser.helpers;

import java.util.regex.Pattern;

public class Constants {
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");

    public static final String SIGN_UP_URL = "https://flex-dot-thesis-lock.ew.r.appspot.com/signup/";

    public static final String SIGN_IN_URL  = "https://flex-dot-thesis-lock.ew.r.appspot.com/login/";
}
