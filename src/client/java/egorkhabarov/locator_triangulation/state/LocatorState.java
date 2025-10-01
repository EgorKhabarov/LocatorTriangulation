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

    public static Map<String, UUID> getNamesMap() {
        Map<String, UUID> names = new HashMap<>();
        if (pos1 != null) {
            for (UUID key : pos1.targets().keySet()) {
                TargetInfo target = pos1.targets().get(key);
                if (target == null || target.name() == null || target.uuid() == null) {
                    continue;
                }
                names.put(target.name(), target.uuid());
            }
        }
        if (pos2 != null) {
            for (UUID key : pos2.targets().keySet()) {
                TargetInfo target = pos2.targets().get(key);
                if (target == null || target.name() == null || target.uuid() == null) {
                    continue;
                }
                names.put(target.name(), target.uuid());
            }
        }
        return names;
    }
}
