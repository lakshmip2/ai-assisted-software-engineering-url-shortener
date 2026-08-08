package com.schwab.aiengineering.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {

            builder.append(
                    CHARACTERS.charAt(
                            random.nextInt(CHARACTERS.length())
                    )
            );
        }

        return builder.toString();
    }

}