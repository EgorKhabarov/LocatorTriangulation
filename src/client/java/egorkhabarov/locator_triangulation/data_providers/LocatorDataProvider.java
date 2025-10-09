package egorkhabarov.locator_triangulation.data_providers;

import egorkhabarov.locator_triangulation.model.LocatorInfo;
import egorkhabarov.locator_triangulation.model.TargetInfo;
import egorkhabarov.locator_triangulation.model.PlayerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.waypoint.TrackedWaypoint;

import java.util.*;

public class LocatorDataProvider {
    public static LocatorInfo getLocatorInfo(MinecraftClient client) {
        if (client == null || client.player == null || client.world == null || client.cameraEntity == null) {
            return null;
        }

        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = player.networkHandler;
        if (networkHandler == null || networkHandler.getWaypointHandler() == null) {
            return null;
        }

        Map<UUID, TargetInfo> targets = new HashMap<>();

        networkHandler.getWaypointHandler().forEachWaypoint(client.cameraEntity, (TrackedWaypoint waypoint) -> {
            try {
                double relativeYaw = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera());
                float playerYaw = client.cameraEntity.getYaw();
                double absYaw = (relativeYaw + playerYaw + 360.0) % 360.0;
                double mcYaw = ((absYaw + 540.0) % 360.0) - 180.0; // [-180,180)

                double distance = Math.sqrt(waypoint.squaredDistanceTo(client.cameraEntity));

                String displayName = waypoint.getSource().map(
                    uuid -> {
                        PlayerListEntry ple = networkHandler.getPlayerListEntry(uuid);
                        if (ple != null && ple.getProfile() != null) {
                            return ple.getProfile().getName();
                        }
                        return "UUID-" + uuid.toString().substring(0, 8);
                    },
                    name -> name
                );

                int color = waypoint.getConfig().color.orElseGet(() -> waypoint.getSource().map(
                    uuid -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, uuid.hashCode()), 0.9F),
                    name -> ColorHelper.withBrightness(ColorHelper.withAlpha(255, name.hashCode()), 0.9F)
                ));

                UUID uuid = waypoint.getSource().left().orElse(null);
                targets.put(uuid, new TargetInfo(uuid, displayName, color, mcYaw, distance));
            } catch (Exception ignored) {}
        });

        PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
        return new LocatorInfo(playerInfo, targets);
    }
}
