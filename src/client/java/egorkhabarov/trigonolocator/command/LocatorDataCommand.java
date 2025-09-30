package egorkhabarov.trigonolocator.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import egorkhabarov.trigonolocator.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import egorkhabarov.trigonolocator.locator.LocatorDataProvider;
import egorkhabarov.trigonolocator.locator.LocatorInfo;
import egorkhabarov.trigonolocator.state.LocatorState;
import egorkhabarov.trigonolocator.util.Triangulation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocatorDataCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // existing debug command
            dispatcher.register(ClientCommandManager.literal("locator_data")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client);
                    if (client.player != null) client.player.sendMessage(Text.literal("[Locator] " + data), false);
                    return 1;
                })
            );

            // pos1
            dispatcher.register(ClientCommandManager.literal("pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
                    if (info == null) {
                        if (client.player != null) client.player.sendMessage(Text.literal("[Locator] Failed to capture pos1").formatted(Formatting.RED), false);
                    } else {
                        LocatorState.setPos1(info);
                        if (client.player != null) client.player.sendMessage(Text.literal("[Locator] pos1 saved"), false);
                    }
                    return 1;
                })
            );

            // pos2
            dispatcher.register(ClientCommandManager.literal("pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
                    if (info == null) {
                        if (client.player != null) client.player.sendMessage(Text.literal("[Locator] Failed to capture pos2").formatted(Formatting.RED), false);
                    } else {
                        LocatorState.setPos2(info);
                        if (client.player != null) client.player.sendMessage(Text.literal("[Locator] pos2 saved"), false);
                    }
                    return 1;
                })
            );

            // clear_pos1
            dispatcher.register(ClientCommandManager.literal("clear_pos1")
                .executes(context -> {
                    LocatorState.clearPos1();
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("[Locator] pos1 cleared"), false);
                    return 1;
                })
            );

            // clear_pos2
            dispatcher.register(ClientCommandManager.literal("clear_pos2")
                .executes(context -> {
                    LocatorState.clearPos2();
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("[Locator] pos2 cleared"), false);
                    return 1;
                })
            );

            // clear_poses
            dispatcher.register(ClientCommandManager.literal("clear_poses")
                .executes(context -> {
                    LocatorState.clearAll();
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("[Locator] pos1 and pos2 cleared"), false);
                    return 1;
                })
            );

            // get_pos1
            dispatcher.register(ClientCommandManager.literal("get_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos1();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    if (client.player != null) client.player.sendMessage(Text.literal("[Locator] pos1: " + data), false);
                    return 1;
                })
            );

            // get_pos2
            dispatcher.register(ClientCommandManager.literal("get_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos2();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    if (client.player != null) client.player.sendMessage(Text.literal("[Locator] pos2: " + data), false);
                    return 1;
                })
            );

            // locate <playername>
            dispatcher.register(
                ClientCommandManager.literal("locate")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .executes(context -> {
                            String target = StringArgumentType.getString(context, "player");
                            MinecraftClient client = MinecraftClient.getInstance();
                            handleLocateSingle(client, target);
                            return 1;
                        })
                    )
            );

            // locate_all
            dispatcher.register(ClientCommandManager.literal("locate_all")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    handleLocateAll(client);
                    return 1;
                })
            );
        });
    }

    private static void handleLocateSingle(MinecraftClient client, String target) {
        if (client.player == null) return;

        LocatorInfo p1 = LocatorState.getPos1();
        LocatorInfo p2 = LocatorState.getPos2();

        if (p1 == null || p2 == null) {
            client.player.sendMessage(Text.literal("[Locator] Need both pos1 and pos2 (use /pos1 and /pos2)").formatted(Formatting.RED), false);
            return;
        }

        Optional<Triangulation.Result> res = Triangulation.triangulate(p1, p2, target);
        if (res.isEmpty()) {
            client.player.sendMessage(Text.literal("[Locator] Player '" + target + "' not found in saved snapshots").formatted(Formatting.RED), false);
            return;
        }

        Triangulation.Result r = res.get();

        client.player.sendMessage(Text.literal(String.format("[Locator] %s -> x=%.3f z=%.3f (error=%.3fm)", target, r.x, r.z, r.error)), false);
    }

    private static void handleLocateAll(MinecraftClient client) {
        if (client.player == null) return;

        LocatorInfo p1 = LocatorState.getPos1();
        LocatorInfo p2 = LocatorState.getPos2();
        if (p1 == null || p2 == null) {
            client.player.sendMessage(Text.literal("[&eLocator&f] Need both pos1 and pos2 (use /pos1 and /pos2)").formatted(Formatting.RED), false);
            return;
        }

        Set<String> names = new HashSet<>();
        for (var t : p1.targets) if (t.name != null) names.add(t.name);
        for (var t : p2.targets) if (t.name != null) names.add(t.name);

        String selfName = client.player.getName().getString();
        int found = 0;
        for (String name : names) {
            if (name.equalsIgnoreCase(selfName)) continue;
            Optional<Triangulation.Result> rOpt = Triangulation.triangulate(p1, p2, name);
            if (rOpt.isPresent()) {
                found++;
                Triangulation.Result r = rOpt.get();
                // client.player.sendMessage(Text.literal(String.format("[Locator] %s -> x=%.3f z=%.3f (error=%.3fm)", name, r.x, r.z, r.error)), false);
                ChatUtils.sendLocatorResult(name, r.x, r.z, r.error);
            }
        }
        if (found == 0) {
            client.player.sendMessage(Text.literal("[Locator] No players located (in snapshots)"), false);
        } else {
            // client.player.sendMessage(Text.literal("[Locator] locate_all finished, found: " + found), false);
        }
    }
}
