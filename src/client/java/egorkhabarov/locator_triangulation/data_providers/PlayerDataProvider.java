package egorkhabarov.locator_triangulation.data_providers;

import egorkhabarov.locator_triangulation.state.PlayerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerDataProvider {
    public static PlayerInfo getPlayerInfo(MinecraftClient client) {
        if (client == null || client.player == null || client.world == null || client.getCameraEntity() == null) {
            return null;
        }
        ClientPlayerEntity player = client.player;
        return new PlayerInfo(player.getX(), player.getZ(), player.getYaw());
    }
}
