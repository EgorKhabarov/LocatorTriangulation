package egorkhabarov.trigonolocator.locator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.TrackedWaypoint.Pitch;

public class LocatorDataProvider {
    public static String getLocatorDebugInfo(MinecraftClient client) {
        if (client == null || client.player == null) {
            return "No client/player";
        }

        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = player.networkHandler;

        if (networkHandler == null || networkHandler.getWaypointHandler() == null) {
            return "No waypoint handler";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Waypoints: ");

        networkHandler.getWaypointHandler().forEachWaypoint(client.cameraEntity, (TrackedWaypoint waypoint) -> {
            try {
                Waypoint.Config config = waypoint.getConfig();
                double distance = Math.sqrt(waypoint.squaredDistanceTo(client.cameraEntity));
                double yaw = waypoint.getRelativeYaw(player.getWorld(), client.gameRenderer.getCamera());
                Pitch pitch = waypoint.getPitch(player.getWorld(), client.gameRenderer);

                String source = waypoint.getSource().map(
                        uuid -> "UUID=" + uuid.toString(),
                        name -> "Name=" + name
                );

                sb.append("{")
                  .append(source)
                  .append(", dist=").append(String.format("%.1f", distance))
                  .append(", yaw=").append(String.format("%.1f", yaw))
                  .append(", pitch=").append(pitch)
                  .append(", style=").append(config.style)
                  .append("} ");
            } catch (Exception e) {
                sb.append("{error: ").append(e.getMessage()).append("} ");
            }
        });

        return sb.toString();
    }
}
