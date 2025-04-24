package dk.pekilidi.date;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
            .withZone(ZoneId.systemDefault());

    public static String formatInstant(Instant instant) {
        return formatter.format(instant);
    }

    public static String formatString(String dateTimeString) {
        var instant = Instant.parse(dateTimeString);
        return formatter.format(instant);
    }

}
