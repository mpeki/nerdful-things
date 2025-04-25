package dk.pekilidi.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Secrets {
    static StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
    static {
        enc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        enc.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
    }

    private Secrets() {
    }

    public static void init(){
        String masterPassword = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if(masterPassword == null) {
            throw new IllegalStateException("JASYPT_ENCRYPTOR_PASSWORD environment variable is not set.");
        }
        enc.setPassword(masterPassword);
        enc.initialize();
    }

    public static String encrypt(String value) {
        if(!enc.isInitialized()) {
            init();
        }
        return enc.encrypt(value);
    }

    public static String decrypt(String encryptedValue) {
        if(!enc.isInitialized()) {
            init();
        }
        return enc.decrypt(encryptedValue);
    }
}
