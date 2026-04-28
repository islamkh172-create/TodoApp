package com.example.todoapp.db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthHelper {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // fallback (never happens on Android)
        }
    }

    public static boolean verify(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }
}
