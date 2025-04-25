package dk.pekilidi.objects;

import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.diff.Change;

import static java.lang.System.*;

public class JaversDiffPrinter {

    private JaversDiffPrinter() {
        // Prevent instantiation
    }

    public static void printSimpleChanges(Diff diff) {
        out.println("=== Simple JaVers Diff Summary ===");

        for (Change change : diff.getChanges()) {
            switch (change) {
                case ValueChange vc -> out.printf("Value changed: %s → %s -> %s%n",
                        vc.getPropertyName(),
                        vc.getLeft(),
                        vc.getRight());
                case ListChange lc -> {
                    out.printf("List change at: %s%n", lc.getPropertyName());
                    lc.getChanges().forEach(c -> out.println("  · " + c));
                }
                case SetChange sc -> {
                    out.printf("Set change at: %s%n", sc.getPropertyName());
                    sc.getChanges().forEach(c -> out.println("  · " + c));
                }
                case MapChange<?> mc -> {
                    out.printf("Map change at: %s%n", mc.getPropertyName());
                    mc.getEntryChanges().forEach(c -> out.println("  · " + c));
                }
                case PropertyChange<?> pc ->
                    // fallback
                        out.printf("Change at: %s%n", pc.getPropertyName());
                default ->
                    // catch-all
                        out.printf("Other change: %s%n", change.toString());
            }
        }

        out.println("===================================");
    }
}
