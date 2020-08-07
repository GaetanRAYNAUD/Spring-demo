package com.example.demo.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public final class RandomUtils {

    private RandomUtils() {}

    private static final int LINKS_SIZE = 30;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        SECURE_RANDOM.nextBytes(new byte[64]);
    }
    public static String generateRandomKey() {
        return RandomStringUtils.random(LINKS_SIZE, 0, 0, true, true, null, SECURE_RANDOM);
    }
}
