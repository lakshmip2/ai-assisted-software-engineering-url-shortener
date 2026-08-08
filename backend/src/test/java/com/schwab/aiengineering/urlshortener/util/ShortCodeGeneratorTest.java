package com.schwab.aiengineering.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator =
            new ShortCodeGenerator();

    @Test
    void shouldGenerateSevenCharacterCode() {

        String code = generator.generate();

        assertNotNull(code);
        assertEquals(7, code.length());
    }

    @Test
    void shouldGenerateOnlyAlphaNumericCharacters() {

        String code = generator.generate();

        assertNotNull(code);

        assertTrue(
                code.matches("[A-Za-z0-9]+"),
                "Generated code should contain only alphanumeric characters"
        );
    }

    @Test
    void shouldGenerateCodeWithExpectedLengthRepeatedly() {

        for (int i = 0; i < 100; i++) {

            String code = generator.generate();

            assertNotNull(code);
            assertEquals(7, code.length());

            assertTrue(
                    code.matches("[A-Za-z0-9]+")
            );
        }
    }

    @Test
    void shouldGenerateDifferentCodes() {

        String code1 = generator.generate();
        String code2 = generator.generate();

        /*
         * SecureRandom makes collisions extremely unlikely.
         * This test verifies normal generator behavior.
         */
        assertNotEquals(code1, code2);
    }

    @Test
    void shouldNotGenerateBlankCode() {

        String code = generator.generate();

        assertNotNull(code);
        assertFalse(code.isBlank());
    }
}