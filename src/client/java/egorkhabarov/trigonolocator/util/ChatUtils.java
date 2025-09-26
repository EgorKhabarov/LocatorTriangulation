package egorkhabarov.trigonolocator.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtils {
    public static void sendClientMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false);
        }
    }
}
