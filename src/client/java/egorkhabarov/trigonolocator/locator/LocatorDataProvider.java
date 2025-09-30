// package egorkhabarov.trigonolocator.locator;
//
// import net.minecraft.client.MinecraftClient;
// import net.minecraft.client.network.ClientPlayerEntity;
// import net.minecraft.client.network.ClientPlayNetworkHandler;
// import net.minecraft.world.waypoint.TrackedWaypoint;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
//
// public class LocatorDataProvider {
//     public static LocatorInfo getLocatorInfo(MinecraftClient client) {
//         if (client == null || client.player == null || client.world == null || client.cameraEntity == null) {
//             return null;
//         }
//
//         ClientPlayerEntity player = client.player;
//         ClientPlayNetworkHandler networkHandler = player.networkHandler;
//         if (networkHandler == null || networkHandler.getWaypointHandler() == null) return null;
//
//         List<LocatorInfo.TargetInfo> targets = new ArrayList<>();
//
//         networkHandler.getWaypointHandler().forEachWaypoint(client.cameraEntity, (TrackedWaypoint waypoint) -> {
//             try {
//                 double relativeYaw = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera());
//                 float playerYaw = client.cameraEntity.getYaw();
//                 double absYaw = (relativeYaw + playerYaw + 360.0) % 360.0;
//                 double mcYaw = ((absYaw + 540.0) % 360.0) - 180.0; // [-180,180)
//
//                 double distance = Math.sqrt(waypoint.squaredDistanceTo(client.cameraEntity));
//                 String displayName = waypoint.getSource().map(
//                         uuid -> {
//                             var p = client.world.getPlayerByUuid(uuid);
//                             return p != null ? p.getName().getString() : ("UUID=" + uuid.toString());
//                         },
//                         name -> name
//                 );
//
//                 UUID uuid = waypoint.getSource().left().orElse(null);
//
//                 targets.add(new LocatorInfo.TargetInfo(uuid, displayName, mcYaw, distance));
//             } catch (Exception ignored) { }
//         });
//
//         return new LocatorInfo(
//             new LocatorInfo.PlayerInfo(player.getX(), player.getY(), player.getZ(), player.getYaw()),
//             targets
//         );
//     }
//
//     public static String getLocatorDebugInfo(MinecraftClient client) {
//         return getLocatorDebugInfo(client, null);
//     }
//
//     public static String getLocatorDebugInfo(MinecraftClient client, LocatorInfo info) {
//         if (info == null) info = getLocatorInfo(client);
//         if (info == null) return "No locator data available";
//
//         StringBuilder sb = new StringBuilder();
//         sb.append("Self: ")
//           .append(String.format("(%.2f, %.2f, %.2f)", info.self.x, info.self.y, info.self.z))
//           .append(" yaw=").append(String.format("%.1f°", info.self.yaw))
//           .append("\n");
//
//         for (LocatorInfo.TargetInfo target : info.targets) {
//             sb.append(" - ").append(target.name)
//               .append(" | yaw=").append(String.format("%.1f°", target.yaw))
//               .append(" | dist=").append(String.format("%.2f", target.distance))
//               .append("\n");
//         }
//         return sb.toString().trim();
//     }
// }


package egorkhabarov.trigonolocator.locator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.waypoint.TrackedWaypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocatorDataProvider {
    public static LocatorInfo getLocatorInfo(MinecraftClient client) {
        if (client == null || client.player == null || client.world == null || client.cameraEntity == null) {
            return null;
        }

        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = player.networkHandler;
        if (networkHandler == null || networkHandler.getWaypointHandler() == null) return null;

        List<LocatorInfo.TargetInfo> targets = new ArrayList<>();

        networkHandler.getWaypointHandler().forEachWaypoint(client.cameraEntity, (TrackedWaypoint waypoint) -> {
            try {
                double relativeYaw = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera());
                float playerYaw = client.cameraEntity.getYaw();
                double absYaw = (relativeYaw + playerYaw + 360.0) % 360.0;
                double mcYaw = ((absYaw + 540.0) % 360.0) - 180.0; // [-180,180)

                double distance = Math.sqrt(waypoint.squaredDistanceTo(client.cameraEntity));

                // Имя игрока или строка
                String displayName = waypoint.getSource().map(
                    uuid -> {
                        PlayerListEntry ple = client.getNetworkHandler().getPlayerListEntry(uuid);
                        if (ple != null && ple.getProfile() != null) {
                            return ple.getProfile().getName();
                        }
                        return "Unknown(" + uuid.toString().substring(0, 8) + ")";
                    },
                    name -> name
                );

                UUID uuid = waypoint.getSource().left().orElse(null);

                targets.add(new LocatorInfo.TargetInfo(uuid, displayName, mcYaw, distance));
            } catch (Exception ignored) { }
        });

        return new LocatorInfo(
            new LocatorInfo.PlayerInfo(player.getX(), player.getY(), player.getZ(), player.getYaw()),
            targets
        );
    }

    public static String getLocatorDebugInfo(MinecraftClient client) {
        return getLocatorDebugInfo(client, null);
    }

    public static String getLocatorDebugInfo(MinecraftClient client, LocatorInfo info) {
        if (info == null) info = getLocatorInfo(client);
        if (info == null) return "No locator data available";

        StringBuilder sb = new StringBuilder();
        sb.append("Self: ")
          .append(String.format("(%.2f, %.2f, %.2f)", info.self.x, info.self.y, info.self.z))
          .append(" yaw=").append(String.format("%.1f°", info.self.yaw))
          .append("\n");

        for (LocatorInfo.TargetInfo target : info.targets) {
            sb.append(" - ").append(target.name)
              .append(" | yaw=").append(String.format("%.1f°", target.yaw))
              .append(" | dist=").append(String.format("%.2f", target.distance))
              .append("\n");
        }
        return sb.toString().trim();
    }
}
