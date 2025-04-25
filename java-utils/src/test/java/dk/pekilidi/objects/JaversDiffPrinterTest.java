package dk.pekilidi.objects;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JaversDiffPrinter utility class.
 */
class JaversDiffPrinterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Javers javers;

    @BeforeEach
    void setUp() {
        // Redirect System.out to capture printed output
        System.setOut(new PrintStream(outContent));

        // Initialize Javers
        javers = JaversBuilder.javers().build();
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    void testPrintSimpleChangesWithValueChange() {
        // Create test objects
        TestObject oldObject = new TestObject("oldValue", 10);
        TestObject newObject = new TestObject("newValue", 10);

        // Compare objects to get diff
        Diff diff = javers.compare(oldObject, newObject);

        // Print the diff
        JaversDiffPrinter.printSimpleChanges(diff);

        // Verify output contains expected value change
        String output = outContent.toString();
        assertTrue(output.contains("=== Simple JaVers Diff Summary ==="));
        assertTrue(output.contains("Value changed: value → oldValue -> newValue"));
        assertTrue(output.contains("==================================="));
    }

    @Test
    void testPrintSimpleChangesWithListChange() {
        // Create test objects with lists
        TestObjectWithList oldObject = new TestObjectWithList(List.of("item1", "item2"));
        TestObjectWithList newObject = new TestObjectWithList(List.of("item1", "item3", "item4"));

        // Compare objects to get diff
        Diff diff = javers.compare(oldObject, newObject);

        // Print the diff
        JaversDiffPrinter.printSimpleChanges(diff);

        // Verify output contains expected list change
        String output = outContent.toString();
        assertTrue(output.contains("=== Simple JaVers Diff Summary ==="));
        assertTrue(output.contains("List change at: items"));
        assertTrue(output.contains("==================================="));
    }

    @Test
    void testPrintSimpleChangesWithSetChange() {
        // Create test objects with sets
        TestObjectWithSet oldObject = new TestObjectWithSet(Set.of("item1", "item2"));
        TestObjectWithSet newObject = new TestObjectWithSet(Set.of("item1", "item3"));

        // Compare objects to get diff
        Diff diff = javers.compare(oldObject, newObject);

        // Print the diff
        JaversDiffPrinter.printSimpleChanges(diff);

        // Verify output contains expected set change
        String output = outContent.toString();
        assertTrue(output.contains("=== Simple JaVers Diff Summary ==="));
        assertTrue(output.contains("Set change at: items"));
        assertTrue(output.contains("==================================="));
    }

    @Test
    void testPrintSimpleChangesWithMapChange() {
        // Create test objects with maps
        Map<String, String> oldMap = new HashMap<>();
        oldMap.put("key1", "value1");
        oldMap.put("key2", "value2");

        Map<String, String> newMap = new HashMap<>();
        newMap.put("key1", "value1");
        newMap.put("key2", "changed");
        newMap.put("key3", "value3");

        TestObjectWithMap oldObject = new TestObjectWithMap(oldMap);
        TestObjectWithMap newObject = new TestObjectWithMap(newMap);

        // Compare objects to get diff
        Diff diff = javers.compare(oldObject, newObject);

        // Print the diff
        JaversDiffPrinter.printSimpleChanges(diff);

        // Verify output contains expected map change
        String output = outContent.toString();
        assertTrue(output.contains("=== Simple JaVers Diff Summary ==="));
        assertTrue(output.contains("Map change at: items"));
        assertTrue(output.contains("==================================="));
    }

    @Test
    void testPrintSimpleChangesWithNoChanges() {
        // Create identical test objects
        TestObject object1 = new TestObject("sameValue", 10);
        TestObject object2 = new TestObject("sameValue", 10);

        // Compare objects to get diff
        Diff diff = javers.compare(object1, object2);

        // Print the diff
        JaversDiffPrinter.printSimpleChanges(diff);

        // Verify output shows summary but no changes
        String output = outContent.toString();
        assertTrue(output.contains("=== Simple JaVers Diff Summary ==="));
        assertTrue(output.contains("==================================="));

        // The output should only contain the header and footer with no changes in between
        assertEquals(2, output.lines().count());
    }

    // Test classes to use with Javers
    private record TestObject(String value, int number) {
    }

    private record TestObjectWithList(List<String> items) {
    }

    private record TestObjectWithSet(Set<String> items) {
    }

    private record TestObjectWithMap(Map<String, String> items) {
    }
}
