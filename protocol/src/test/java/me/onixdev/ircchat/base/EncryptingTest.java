package me.onixdev.ircchat.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptingTest {

    @BeforeEach
    void setUp() {
        Encrypting.INSTANCE.setKey("testSecretKey123");
    }

    @Test
    void encryptReturnsNonEmptyBase64() {
        String result = Encrypting.INSTANCE.encrypt("hello");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void decryptReversesEncrypt() {
        String original = "Hello, World!";
        String encrypted = Encrypting.INSTANCE.encrypt(original);
        String decrypted = Encrypting.INSTANCE.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void decryptReversesEncryptWithUnicode() {
        String original = "Привет мир!";
        String encrypted = Encrypting.INSTANCE.encrypt(original);
        String decrypted = Encrypting.INSTANCE.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void encryptEmptyString() {
        String encrypted = Encrypting.INSTANCE.encrypt("");
        String decrypted = Encrypting.INSTANCE.decrypt(encrypted);
        assertEquals("", decrypted);
    }

    @Test
    void differentKeysProduceDifferentOutput() {
        String original = "sametext";
        Encrypting.INSTANCE.setKey("key1");
        String enc1 = Encrypting.INSTANCE.encrypt(original);

        Encrypting.INSTANCE.setKey("key2");
        String enc2 = Encrypting.INSTANCE.encrypt(original);

        assertNotEquals(enc1, enc2);

        Encrypting.INSTANCE.setKey("testSecretKey123");
    }

    @Test
    void encryptIsDeterministic() {
        String enc1 = Encrypting.INSTANCE.encrypt("test");
        String enc2 = Encrypting.INSTANCE.encrypt("test");
        assertEquals(enc1, enc2);
    }

    @Test
    void encryptOutputDiffersFromInput() {
        String input = "plaintext";
        String encrypted = Encrypting.INSTANCE.encrypt(input);
        assertNotEquals(input, encrypted);
    }

    @Test
    void keyGetterAndSetter() {
        Encrypting.INSTANCE.setKey("mykey");
        assertEquals("mykey", Encrypting.INSTANCE.getKey());
        Encrypting.INSTANCE.setKey("testSecretKey123");
    }
}
