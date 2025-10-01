package egorkhabarov.locator_triangulation.state;

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
}
