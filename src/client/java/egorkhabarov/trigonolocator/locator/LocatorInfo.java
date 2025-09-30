package egorkhabarov.trigonolocator.locator;

import java.util.List;
import java.util.UUID;

public class LocatorInfo {
    public final PlayerInfo self;
    public final List<TargetInfo> targets;

    public LocatorInfo(PlayerInfo self, List<TargetInfo> targets) {
        this.self = self;
        this.targets = targets;
    }

    public static class PlayerInfo {
        public final double x, y, z;
        public final double yaw;

        public PlayerInfo(double x, double y, double z, double yaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }
    }

    public static class TargetInfo {
        public final UUID uuid;      // может быть null если источник — имя
        public final String name;
        public final double yaw;     // абсолютный yaw [-180,180)
        public final double distance; // 3D расстояние (пока только для справки)

        public TargetInfo(UUID uuid, String name, double yaw, double distance) {
            this.uuid = uuid;
            this.name = name;
            this.yaw = yaw;
            this.distance = distance;
        }
    }
}
