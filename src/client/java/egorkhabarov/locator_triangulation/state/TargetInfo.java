package egorkhabarov.locator_triangulation.state;

import java.util.UUID;

public class TargetInfo {
    public final UUID uuid;
    public final String name;
    public final double yaw;  // Абсолютный yaw [-180,180)
    public final double distance;

    public TargetInfo(UUID uuid, String name, double yaw, double distance) {
        this.uuid = uuid;
        this.name = name;
        this.yaw = yaw;
        this.distance = distance;
    }
}
