package egorkhabarov.locator_triangulation.state;

public class PlayerInfo {
    public final double x, y, z;
    public final double yaw;  // Абсолютный yaw [-180,180)

    public PlayerInfo(double x, double y, double z, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }
}
