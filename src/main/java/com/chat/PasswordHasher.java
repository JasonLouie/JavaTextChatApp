package com.chat;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private static final int WORK_FACTOR = 12;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
