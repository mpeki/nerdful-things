package dk.pekilidi.input;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;


@Slf4j
public class PromptUtil {

    public static final String NO_DEFAULT_VALUE = "no-default-value";

    public static String prompt(String message, boolean isPassword, String defaultValue) {

        String result = "";
        log.info(message);
        Console console = System.console();
        if (console == null) {
            // Fallback when no console is available. You could still use a BufferedReader,
            // but it won't hide input.
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                return reader.readLine();
            } catch (IOException e) {
                log.error("Error reading input from console", e);
                return "";
            }
        }
        return getInput(console, isPassword, defaultValue);
    }

    private static String getInput(Console console, boolean isPassword, String defaultValue) {

        while (true) {
            String result = null;
            if (isPassword) {
                // readPassword() does not display characters
                result = new String(console.readPassword());
            } else {
                // Normal reading (visible input)
                result = console.readLine();
            }
            if (result != null && !result.isEmpty()) {
                return result;
            } else if (!defaultValue.equalsIgnoreCase(NO_DEFAULT_VALUE)) {
                return defaultValue;

            } else {
                console.printf("Please enter a valid input.%n");
            }
        }
    }
}
