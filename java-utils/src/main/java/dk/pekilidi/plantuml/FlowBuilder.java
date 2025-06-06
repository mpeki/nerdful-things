package dk.pekilidi.plantuml;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static java.util.Objects.nonNull;

@Slf4j
public class FlowBuilder {

    @Getter
    private final String flowName;
    private final StringBuilder umlBuilder;

    private String imageFileName = null;
    private boolean closed = false; // Keep track if we already appended '@enduml'

    // Private constructor
    private FlowBuilder(String flowName) {
        this.flowName = flowName;
        this.umlBuilder = new StringBuilder();
        this.umlBuilder.append("@startuml\n");
    }

    // Factory method to create a new FlowBuilder
    public static FlowBuilder create(String flowName) {
        return new FlowBuilder(flowName);
    }

    public FlowBuilder addParticipant(Participant participant) {
        if(nonNull(participant) && nonNull(participant.getTitle())){
            umlBuilder.append("participant ")
                    .append(participant.title.replaceAll("[^a-zA-Z0-9]", ""))
                    .append(" [\n")
                    .append("\t=").append(participant.title).append('\n')
                    .append("\t\"\"").append(participant.subTitle == null ? "\t\t" : participant.subTitle).append("\"\"").append("\n")
                    .append("]\n\n");
        }
        return this;
    }

    /**
     * Sets the title of the diagram.
     *
     * @param diagramTitle the title to display in the PlantUML diagram
     * @return this builder instance for fluent chaining
     */
    public FlowBuilder title(String diagramTitle) {
        umlBuilder.append("\n").append("title ").append(diagramTitle).append("\n");
        return this;
    }

    /**
     * Adds a request arrow from one participant to another.
     *
     * @param from    the participant who initiates the request
     * @param to      the participant who receives the request
     * @param content the message or label on the arrow
     * @return this builder instance for fluent chaining
     */
    public FlowBuilder request(String from, String to, String content) {
        umlBuilder
                .append('"')
                .append(from)
                .append('"')
                .append(" -> ")
                .append('"')
                .append(to)
                .append('"')
                .append(" : ")
                .append(content)
                .append("\n");
        return this;
    }

    public FlowBuilder request(Participant from, Participant to, String content) {
        umlBuilder
                .append(from.title.replaceAll("[^a-zA-Z0-9]", ""))
                .append(" -> ")
                .append(to.title.replaceAll("[^a-zA-Z0-9]", ""))
                .append(" : ")
                .append(content)
                .append("\n");
        return this;
    }


    /**
     * Adds a request arrow from one participant to another.
     *
     * @param from    the participant who initiates the request
     * @param to      the participant who receives the request
     * @param content the message or label on the arrow
     * @return this builder instance for fluent chaining
     */
    public FlowBuilder request(String from, String to, String content, boolean error) {
        umlBuilder
                .append('"')
                .append(from+"\n(test)")
                .append('"')
                .append(error ? " ->x " : " -> ")
                .append('"')
                .append(to)
                .append('"')
                .append(" : ")
                .append(content)
                .append("\n");
        return this;
    }


    /**
     * Adds a response arrow from one participant to another.
     *
     * @param from    the participant who initiates the response
     * @param to      the participant who receives the response
     * @param content the message or label on the arrow
     * @return this builder instance for fluent chaining
     */
    public FlowBuilder response(String from, String to, String content) {
        // If you want a different arrow style for "response", just change -> to --> or similar
        umlBuilder
                .append('"')
                .append(from)
                .append('"')
                .append(" --> ")
                .append('"')
                .append(to)
                .append('"')
                .append(" : ")
                .append(content)
                .append("\n");
        return this;
    }

    public FlowBuilder response(Participant from, Participant to, String content) {
        umlBuilder
                .append(from.title.replaceAll("[^a-zA-Z0-9]", ""))
                .append(" -> ")
                .append(to.title.replaceAll("[^a-zA-Z0-9]", ""))
                .append(" : ")
                .append(content)
                .append("\n");
        return this;
    }

    /**
     * Completes the diagram with "@enduml" and returns the entire UML text.
     *
     * @return the full PlantUML sequence diagram string
     */
    public String build() {
        if (!closed) {
            umlBuilder.append("@enduml\n");
            closed = true;
        }
        return umlBuilder.toString();
    }

    /**
     * Convenience method to print the output to System.out
     */
    public void printMarkup() {
        System.out.println(build());
    }

    @Getter
    @Builder
    @ToString
    public static class Participant {
        private String title;
        private String subTitle;

        public String print() {
            return "%s (%s)".formatted(title, subTitle);
        }
    }


    // Example usage
    public static void main(String[] args) throws IOException {
        FlowBuilder.Participant requesting = FlowBuilder.Participant.builder().title("Codan").subTitle("VIR_CODAN").build();
        FlowBuilder.Participant responding = FlowBuilder.Participant.builder().title("Gjensidige").subTitle("VIR_GJEN").build();
        FlowBuilder.Participant connectorPlus = FlowBuilder.Participant.builder().title("CO+").build();
        FlowBuilder.Participant fAndP = FlowBuilder.Participant.builder().title("F&P").subTitle("fogp").build();

//        Participant codan = Participant.builder().title("Codan").subTitle("VIR121212").build();
//        Participant coplus = Participant.builder().title("CO").subTitle("ProcessFactory").build();
//        Participant fogp = Participant.builder().title("F&P").subTitle("FogP").build();
        FlowBuilder.create("testFlow")
                .title("Inbound Exchange")
                .addParticipant(requesting)
                .addParticipant(connectorPlus)
                .addParticipant(fAndP)
                .addParticipant(responding)
                .request(requesting, connectorPlus, "request")
//                .response(coplus, coplus, "Fejl")
                .writeAsHtml("target/inbound-exchange.html");
//                .writeAsImage("target/inbound-exchange.png", FileFormat.PNG);
    }

    public void print() throws IOException {
        // Create a reader from the source
        SourceStringReader reader = new SourceStringReader(build());

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
        }
    }

    public void writeReport() throws IOException {
        // Create a reader from the source
        SourceStringReader reader = new SourceStringReader(build());

        // We use a ByteArrayOutputStream to capture the output
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Generate the diagram in ASCII format
            reader.outputImage(
                    outputStream,
                    new FileFormatOption(FileFormat.HTML) // or FileFormat.UTXT for Unicode text
            );

            // Convert the output bytes to a string
            String asciiDiagram = outputStream.toString(StandardCharsets.UTF_8);
            log.info(asciiDiagram);
        }
    }

    /**
     * Writes the diagram to a file in a given format (e.g., PNG, SVG, UTXT, etc.).
     *
     * @param filePath   The path (including filename) where the diagram will be saved.
     * @throws IOException if an I/O error occurs.
     */
    public FlowBuilder writeAsImage(String filePath) throws IOException {

        imageFileName = Paths.get(filePath).getFileName().toString();
        SourceStringReader reader = new SourceStringReader(build());

        try (OutputStream out = new FileOutputStream(filePath)) {
            // Generate the diagram in the specified format
            reader.outputImage(out, new FileFormatOption(FileFormat.SVG));
        }
        return this;
    }

    /**
     * Writes the diagram as an HTML file containing an embedded Base64-encoded SVG image.
     *
     * @param filePath The path (including filename) where the HTML file will be saved.
     * @throws IOException if an I/O error occurs.
     */
    public void writeAsHtml(String filePath) throws IOException {
        SourceStringReader reader = new SourceStringReader(build());

        // Generate an SVG image in memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reader.outputImage(outputStream, new FileFormatOption(FileFormat.SVG));

        // Base64-encode the SVG
//        String encodedSvg = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        // Create a simple HTML page with the embedded SVG
        String htmlContent =
                "<html>\n"
                        + "<head>\n"
                        + "  <meta charset=\"UTF-8\">\n"
                        + "  <title>PlantUML Diagram</title>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "  <object data=\"" + imageFileName + "\" type=\"image/svg+xml\"/>\n"
                        + "</body>\n"
                        + "</html>\n";

        // Write the HTML content to the specified file
        Files.write(Paths.get(filePath), htmlContent.getBytes(StandardCharsets.UTF_8));
    }
}
