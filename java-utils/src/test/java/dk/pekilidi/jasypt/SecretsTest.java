package dk.pekilidi.jasypt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junitpioneer.jupiter.SetEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Secrets utility class.
 * 
 * Note: Due to limitations in the Jasypt library, the StandardPBEStringEncryptor
 * can only be initialized once. Therefore, these tests must run in a specific order
 * and cannot reinitialize the encryptor between tests.
 */
@TestMethodOrder(OrderAnnotation.class)
@SetEnvironmentVariable(key = "JASYPT_ENCRYPTOR_PASSWORD", value = "testMasterPassword")
class SecretsTest {

    private static final String TEST_VALUE = "sensitiveInformation";

    @BeforeAll
    static void setUp() {
    }

    @Test
    @Order(1)
    void testEncrypt() {
        // Encrypt a test value
        String encrypted = Secrets.encrypt(TEST_VALUE);

        // Verify that the encrypted value is not null or empty
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());

        // Verify that the encrypted value is different from the original
        assertNotEquals(TEST_VALUE, encrypted);
    }

    @Test
    @Order(2)
    void testDecrypt() {
        // First encrypt a value
        String encrypted = Secrets.encrypt(TEST_VALUE);

        // Then decrypt it
        String decrypted = Secrets.decrypt(encrypted);

        // Verify that the decrypted value matches the original
        assertEquals(TEST_VALUE, decrypted);
    }

    @Test
    @Order(3)
    void testRoundTrip() {
        // Test multiple values to ensure consistency
        String[] testValues = {
            "simple text",
            "text with spaces and punctuation!",
            "1234567890",
            "Special chars: @#$%^&*()",
            "Unicode: 你好, こんにちは, 안녕하세요"
        };

        for (String value : testValues) {
            String encrypted = Secrets.encrypt(value);
            String decrypted = Secrets.decrypt(encrypted);
            assertEquals(value, decrypted, "Round trip failed for: " + value);
        }
    }

    @Test
    @Order(4)
    void testDifferentEncryptionResults() {
        // Encrypting the same value twice should yield different results due to IV
        String encrypted1 = Secrets.encrypt(TEST_VALUE);
        String encrypted2 = Secrets.encrypt(TEST_VALUE);

        // The encrypted values should be different due to random IV
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    @Order(5)
    void testEncryptedValueFormat() {
        // Encrypt a test value
        String encrypted = Secrets.encrypt(TEST_VALUE);

        // Verify that the encrypted value has the expected format
        // Jasypt encrypted values are Base64 encoded
        assertTrue(encrypted.matches("^[A-Za-z0-9+/=]+$"), "Encrypted value should be Base64 encoded");

        // Encrypted value should be longer than the original due to IV and padding
        assertTrue(encrypted.length() > TEST_VALUE.length(), "Encrypted value should be longer than original");
    }

    @Test
    @Order(6)
    void testEmptyValues() {
        // Test with empty string
        String emptyEncrypted = Secrets.encrypt("");
        assertNotNull(emptyEncrypted);
        assertFalse(emptyEncrypted.isEmpty());
        assertEquals("", Secrets.decrypt(emptyEncrypted));
    }

    @Test
    @Order(7)
    void testNullValues() {
        // Test encrypt with null
        String nullEncrypted = Secrets.encrypt(null);
        // Based on observed behavior, encrypt(null) returns null
        assertNull(nullEncrypted, "Encrypted null should be null");

        // Test decrypt with null
        String nullDecrypted = Secrets.decrypt(null);
        // Based on observed behavior, decrypt(null) returns null
        assertNull(nullDecrypted, "Decrypted null should be null");

        // Verify that null values are handled consistently
        assertEquals(nullEncrypted, nullDecrypted, "Encrypt and decrypt should handle null consistently");
    }
}
