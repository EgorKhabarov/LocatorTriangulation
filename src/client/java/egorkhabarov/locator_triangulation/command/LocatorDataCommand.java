package egorkhabarov.locator_triangulation.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import egorkhabarov.locator_triangulation.locator.LocatorDataProvider;
import egorkhabarov.locator_triangulation.state.LocatorInfo;
import egorkhabarov.locator_triangulation.state.LocatorState;
import egorkhabarov.locator_triangulation.util.Triangulation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LocatorDataCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("locator_data")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client);
                    ChatUtils.sendInfoMessage(data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
                    if (info == null) {
                        ChatUtils.sendErrorMessage("Failed to capture pos1");
                    } else {
                        LocatorState.setPos1(info);
                        ChatUtils.sendConfirmationMessage("pos1 saved");
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
                    if (info == null) {
                        ChatUtils.sendErrorMessage("Failed to capture pos2");
                    } else {
                        LocatorState.setPos2(info);
                        ChatUtils.sendConfirmationMessage("pos2 saved");
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_pos1")
                .executes(context -> {
                    LocatorState.clearPos1();
                    ChatUtils.sendConfirmationMessage("pos1 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_pos2")
                .executes(context -> {
                    LocatorState.clearPos2();
                    ChatUtils.sendConfirmationMessage("pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_poses")
                .executes(context -> {
                    LocatorState.clearAll();
                    ChatUtils.sendConfirmationMessage("pos1 and pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos1();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    ChatUtils.sendInfoMessage("pos1: " + data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos2();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    ChatUtils.sendInfoMessage("pos2: " + data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_poses")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    ChatUtils.sendInfoMessage(
                        "pos1: "
                            + LocatorDataProvider.getLocatorDebugInfo(client, LocatorState.getPos1())
                            + "\npos2: "
                            + LocatorDataProvider.getLocatorDebugInfo(client, LocatorState.getPos2())
                    );
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

            dispatcher.register(ClientCommandManager.literal("locate_all")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    handleLocateAll(client);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("end_portal_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("end_portal_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("locate_end_portal")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_end_portal_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_end_portal_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_end_portal_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_end_portal_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_end_portal_poses")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
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
            ChatUtils.sendErrorMessage("Need both pos1 and pos2 (use /pos1 and /pos2)");
            return;
        }

        Optional<Triangulation.Result> res = Triangulation.triangulate(p1, p2, target);
        if (res.isEmpty()) {
            ChatUtils.sendErrorMessage(String.format("Player \"%s\" not found in saved snapshots", target));
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
            ChatUtils.sendErrorMessage("Need both pos1 and pos2 (use /pos1 and /pos2)");
            return;
        }

        Set<String> names = new HashSet<>();
        for (var t : p1.targets) if (t.name() != null) names.add(t.name());
        for (var t : p2.targets) if (t.name() != null) names.add(t.name());

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
            ChatUtils.sendErrorMessage("No players located (in snapshots)");
        }
    }
}
