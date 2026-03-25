package egorkhabarov.locator_triangulation.data_providers;

import egorkhabarov.locator_triangulation.model.LocatorInfo;
import egorkhabarov.locator_triangulation.model.TargetInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.Level;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.waypoints.TrackedWaypoint;

import java.util.*;

public class LocatorDataProvider {
    public static LocatorInfo getLocatorInfo(Minecraft client) {
        if (client == null || client.player == null || client.level == null || client.getCameraEntity() == null) {
            return null;
        }

        LocalPlayer player = client.player;
        ClientPacketListener connection = player.connection;
        ClientWaypointManager waypointManager = connection.getWaypointManager();

        Map<UUID, TargetInfo> targets = new HashMap<>();
        Entity camera = client.getCameraEntity();
        Level level = client.level;

        waypointManager.forEachWaypoint(camera, (TrackedWaypoint waypoint) -> {
            try {
                UUID uuid = waypoint.id().left().orElse(null);
                if (uuid == null) {
                    return;
                }
                double relativeYaw = waypoint.yawAngleToCamera(level, client.gameRenderer.getMainCamera(), entityx -> 1.0f);

                float playerYaw = camera.getYRot();
                double absYaw = (relativeYaw + playerYaw + 360.0) % 360.0;
                double mcYaw = ((absYaw + 540.0) % 360.0) - 180.0; // [-180,180)

                double distance = Math.sqrt(waypoint.distanceSquared(camera));

                String displayName = Optional.ofNullable(connection.getPlayerInfo(uuid))
                    .map((PlayerInfo info) -> info.getProfile().name())
                    .orElse("UUID-" + uuid.toString().substring(0, 8));
                int color = waypoint.icon().color.orElseGet(() -> waypoint.id().map(
                    uUID -> ARGB.setBrightness(ARGB.color(255, uUID.hashCode()), 0.9F),
                    string -> ARGB.setBrightness(ARGB.color(255, string.hashCode()), 0.9F)
                ));
                targets.put(uuid, new TargetInfo(uuid, displayName, color, mcYaw, distance));
            } catch (Exception ignored) {}
        });

        egorkhabarov.locator_triangulation.model.PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
        return new LocatorInfo(playerInfo, targets);
    }
}
