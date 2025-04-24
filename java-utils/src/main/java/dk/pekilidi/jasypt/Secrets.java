package dk.pekilidi.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Secrets {
    static StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();

    public static void init(String masterPassword) {
        enc.setPassword(masterPassword);
        enc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        enc.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
    }

    public static String encrypt(String value) {
        return enc.encrypt(value);
    }

    public static String decrypt(String encryptedValue) {
        return enc.decrypt(encryptedValue);
    }
}
