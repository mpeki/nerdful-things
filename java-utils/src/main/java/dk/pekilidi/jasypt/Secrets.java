package dk.pekilidi.jasypt;

import static org.passay.DigestDictionaryRule.ERROR_CODE;

import java.util.List;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

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
		if(masterPassword == null || masterPassword.isBlank()) {
			masterPassword = System.getProperty("JASYPT_ENCRYPTOR_PASSWORD");
		}
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

	public static String generateRandomPassword(int length) {
		PasswordGenerator gen = new PasswordGenerator();
		CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
		lowerCaseRule.setNumberOfCharacters(2);
		CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
		upperCaseRule.setNumberOfCharacters(2);

		CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
		digitRule.setNumberOfCharacters(2);

		CharacterData specialChars = new CharacterData() {
			public String getErrorCode() {
				return ERROR_CODE;
			}

			public String getCharacters() {
				return "!@#$%^&*()_+";
			}
		};

		CharacterRule splCharRule = new CharacterRule(specialChars);
		splCharRule.setNumberOfCharacters(2);

		return gen.generatePassword(length, List.of( lowerCaseRule, upperCaseRule, digitRule, splCharRule));
	}
}
