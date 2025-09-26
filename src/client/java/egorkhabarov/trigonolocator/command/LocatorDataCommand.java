package egorkhabarov.trigonolocator.command;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import egorkhabarov.trigonolocator.locator.LocatorDataProvider;

public class LocatorDataCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("locator_data")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client);
                    client.player.sendMessage(Text.literal("[Locator] " + data), false);
                    return 1;
                }));
        });
    }
}
