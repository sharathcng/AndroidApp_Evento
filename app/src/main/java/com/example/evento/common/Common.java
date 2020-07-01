package com.example.evento.common;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Common {

    public static FirebaseUser currentUser;

    public static String convertCodeToStatus(String string) {

        if (string.equals("0"))
            return "organized";
        else if (string.equals("1"))
            return "2 days to go";
        else if (string.equals("2"))
            return "1 day to go";
        else if (string.equals("3"))
            return "Today";
        else if (string.equals("4"))
            return "postponed";
        else
            return "preponed";

    }
}
