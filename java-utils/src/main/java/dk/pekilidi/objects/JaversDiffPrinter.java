package dk.pekilidi.objects;

import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.diff.Change;

public class JaversDiffPrinter {

    public static void printSimpleChanges(Diff diff) {
        System.out.println("=== Simple JaVers Diff Summary ===");

        for (Change change : diff.getChanges()) {
            if (change instanceof ValueChange) {
                ValueChange vc = (ValueChange) change;
                System.out.printf("Value changed: %s → %s -> %s%n",
                        vc.getPropertyName(),
                        vc.getLeft(),
                        vc.getRight());
            } else if (change instanceof ListChange) {
                ListChange lc = (ListChange) change;
                System.out.printf("List change at: %s%n", lc.getPropertyName());
                lc.getChanges().forEach(c -> System.out.println("  · " + c));
            } else if (change instanceof SetChange) {
                SetChange sc = (SetChange) change;
                System.out.printf("Set change at: %s%n", sc.getPropertyName());
                sc.getChanges().forEach(c -> System.out.println("  · " + c));
            } else if (change instanceof MapChange) {
                MapChange mc = (MapChange) change;
                System.out.printf("Map change at: %s%n", mc.getPropertyName());
                mc.getEntryChanges().forEach(c -> System.out.println("  · " + c));
            } else if (change instanceof PropertyChange) {
                // fallback
                PropertyChange pc = (PropertyChange) change;
                System.out.printf("Change at: %s%n", pc.getPropertyName());
            } else {
                // catch-all
                System.out.printf("Other change: %s%n", change.toString());
            }
        }

        System.out.println("===================================");
    }
}
