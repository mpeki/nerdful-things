package dk.pekilidi.plantuml;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UmlWriter {

    public static void main(String[] args) throws Exception {

        String umlSource = FlowBuilder.create("testFlow")
                .title("Inbound Exchange")
                .request("VIR121212", "CO+", "Query Request", true)
                .response("CO+", "VIR121212", "Http 200 OK")
                .request("CO+", "F&P", "Query Request")
                .response("F&P", "CO+", "Ack")
                .build();

        System.out.println(umlSource);

        // Create a reader from the source
        SourceStringReader reader = new SourceStringReader(umlSource);

        // We use a ByteArrayOutputStream to capture the output
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Generate the diagram in ASCII format
            reader.outputImage(
                    outputStream,
                    new FileFormatOption(FileFormat.UTXT) // or FileFormat.UTXT for Unicode text
            );

            // Convert the output bytes to a string
            String asciiDiagram = outputStream.toString(StandardCharsets.UTF_8);
            System.out.println(asciiDiagram);
        }
    }

    public static final void write(String umlSource) throws IOException {
        // Create a reader from the source
        SourceStringReader reader = new SourceStringReader(umlSource);

        // We use a ByteArrayOutputStream to capture the output
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Generate the diagram in ASCII format
            reader.outputImage(
                    outputStream,
                    new FileFormatOption(FileFormat.UTXT) // or FileFormat.UTXT for Unicode text
            );

            // Convert the output bytes to a string
            String asciiDiagram = outputStream.toString(StandardCharsets.UTF_8);
            log.info(asciiDiagram);
//            System.out.println(asciiDiagram);
        }

    }
}
