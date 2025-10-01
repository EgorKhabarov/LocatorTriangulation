package egorkhabarov.locator_triangulation.state;

public class TriangulationState {
    private static PlayerInfo pos1;
    private static PlayerInfo pos2;

    public static void setPos1(PlayerInfo info) {pos1 = info;}
    public static void setPos2(PlayerInfo info) {pos2 = info;}
    public static PlayerInfo getPos1() {return pos1;}
    public static PlayerInfo getPos2() {return pos2;}
    public static void clearPos1() {pos1 = null;}
    public static void clearPos2() {pos2 = null;}
    public static void clearAll() {pos1 = null; pos2 = null;}
}
