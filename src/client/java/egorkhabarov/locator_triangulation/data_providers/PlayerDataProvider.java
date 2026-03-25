package egorkhabarov.locator_triangulation.data_providers;

import egorkhabarov.locator_triangulation.model.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class PlayerDataProvider {
    public static PlayerInfo getPlayerInfo(Minecraft client) {
        if (client == null || client.player == null || client.level == null || client.getCameraEntity() == null) {
            return null;
        }
        LocalPlayer player = client.player;
        return new PlayerInfo(player.getX(), player.getZ(), player.getYRot());
    }
}
