package egorkhabarov.locator_triangulation.state;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class LocatorState {
    private static LocatorInfo pos1;
    private static LocatorInfo pos2;

    public static void setPos1(LocatorInfo info) {pos1 = info;}
    public static void setPos2(LocatorInfo info) {pos2 = info;}
    public static LocatorInfo getPos1() {return pos1;}
    public static LocatorInfo getPos2() {return pos2;}
    public static void clearPos1() {pos1 = null;}
    public static void clearPos2() {pos2 = null;}
    public static void clearAll() {pos1 = null; pos2 = null;}

    /**
     * A dictionary of names that can be calculated
     * @return .
     */
    public static Map<String, UUID> getNamesMap() {
        if (pos1 == null || pos2 == null) {
            return new HashMap<>();
        }
        Map<String, UUID> names = new HashMap<>();

        for (UUID uuid : pos1.targets().keySet()) {
            TargetInfo target1 = pos1.targets().get(uuid);
            if (target1 == null || target1.name() == null || target1.uuid() == null) {
                continue;
            }

            if (pos2.targets().containsKey(uuid)) {
                TargetInfo target2 = pos2.targets().get(uuid);
                if (target2 == null || target2.name() == null || target2.uuid() == null) {
                    continue;
                }

                if (target1.uuid().equals(target2.uuid())) {
                    names.put(target1.name(), target1.uuid());
                }
            }
        }
        return names;
    }
}
