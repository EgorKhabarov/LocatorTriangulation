package egorkhabarov.locator_triangulation.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import egorkhabarov.locator_triangulation.data_providers.PlayerDataProvider;
import egorkhabarov.locator_triangulation.state.*;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import egorkhabarov.locator_triangulation.data_providers.LocatorDataProvider;
import egorkhabarov.locator_triangulation.util.Triangulation;

import java.util.*;

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

            dispatcher.register(ClientCommandManager.literal("locator_pos1")
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

            dispatcher.register(ClientCommandManager.literal("locator_pos2")
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

            dispatcher.register(ClientCommandManager.literal("clear_locator_pos1")
                .executes(context -> {
                    LocatorState.clearPos1();
                    ChatUtils.sendConfirmationMessage("pos1 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_locator_pos2")
                .executes(context -> {
                    LocatorState.clearPos2();
                    ChatUtils.sendConfirmationMessage("pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_locator_poses")
                .executes(context -> {
                    LocatorState.clearAll();
                    ChatUtils.sendConfirmationMessage("pos1 and pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_locator_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos1();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    ChatUtils.sendInfoMessage("pos1: " + data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_locator_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    LocatorInfo info = LocatorState.getPos2();
                    String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    ChatUtils.sendInfoMessage("pos2: " + data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_locator_poses")
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
                ClientCommandManager.literal("locator_locate")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        // .suggests((context, builder) -> {
                        //     MinecraftClient client = MinecraftClient.getInstance();
                        //     if (client.world != null) {
                        //         for (PlayerEntity p : client.world.getPlayers()) {
                        //             System.out.println(p.getGameProfile().getName());
                        //         }
                        //     }
                        //     return builder.buildFuture();
                        // })
                        .suggests((context, builder) -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            Set<String> names = new HashSet<>(LocatorState.getNamesMap().keySet());
                            if (client.world != null) {
                                for (PlayerEntity p : client.world.getPlayers()) {
                                    names.add(p.getGameProfile().getName());
                                }
                            }
                            if (client.player != null) {
                                String selfName = client.player.getName().getString();
                                for (String name : names) {
                                    if (name.equalsIgnoreCase(selfName)) {
                                        continue;
                                    }
                                    builder.suggest(name);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            String target_name = StringArgumentType.getString(context, "player");
                            Map<String, UUID> namesMap = LocatorState.getNamesMap();
                            UUID uuid = namesMap.get(target_name);
                            if (uuid == null) {
                                ChatUtils.sendErrorMessage(String.format("Player \"%s\" not found in saved snapshots", target_name));
                            }
                            handleLocateSingle(client, uuid, target_name);
                            return 1;
                        })
                    )
            );

            dispatcher.register(ClientCommandManager.literal("locator_locate_all")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    handleLocateAll(client);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("end_portal_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
                    if (playerInfo == null) {
                        ChatUtils.sendErrorMessage("Failed to capture pos1");
                    } else {
                        TriangulationState.setPos1(playerInfo);
                        ChatUtils.sendConfirmationMessage("pos1 saved");
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("end_portal_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
                    if (playerInfo == null) {
                        ChatUtils.sendErrorMessage("Failed to capture pos2");
                    } else {
                        TriangulationState.setPos2(playerInfo);
                        ChatUtils.sendConfirmationMessage("pos2 saved");
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("triangulation_locate")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    // TODO
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_triangulation_pos1")
                .executes(context -> {
                    TriangulationState.clearPos1();
                    ChatUtils.sendConfirmationMessage("pos1 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_triangulation_pos2")
                .executes(context -> {
                    TriangulationState.clearPos2();
                    ChatUtils.sendConfirmationMessage("pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("clear_triangulation_poses")
                .executes(context -> {
                    TriangulationState.clearAll();
                    ChatUtils.sendConfirmationMessage("pos1 and pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_triangulation_pos1")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    // LocatorInfo info = LocatorState.getPos1();
                    // String data = LocatorDataProvider.getLocatorDebugInfo(client, info);
                    // ChatUtils.sendInfoMessage("pos1: " + data);
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_triangulation_pos2")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("get_triangulation_poses")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    return 1;
                })
            );
        });
    }

    private static Optional<Triangulation.Result> getLocateSingle(UUID uuid) {
        LocatorInfo pos1 = LocatorState.getPos1();
        LocatorInfo pos2 = LocatorState.getPos2();

        if (pos1 == null || pos2 == null) {
            ChatUtils.sendErrorMessage("Need both pos1 and pos2");
            return Optional.empty();
        }
        PlayerInfo self1 = pos1.self();
        PlayerInfo self2 = pos2.self();
        double x1 = self1.x(), z1 = self1.z();
        double x2 = self2.x(), z2 = self2.z();

        TargetInfo target1 = pos1.targets().get(uuid);
        TargetInfo target2 = pos2.targets().get(uuid);

        if (target1 == null || target2 == null) {
            return Optional.empty();
        }
        double yaw1 = target1.yaw(), yaw2 = target2.yaw();

        return Triangulation.triangulate(
            new PlayerInfo(x1, z1, yaw1),
            new PlayerInfo(x2, z2, yaw2)
        );
    }

    private static void handleLocateSingle(MinecraftClient client, UUID uuid, String target_name) {
        if (client.player == null) return;

        Optional<Triangulation.Result> result = LocatorDataCommand.getLocateSingle(uuid);
        if (result.isEmpty()) {
            ChatUtils.sendErrorMessage(String.format("Player \"%s\" not found in saved snapshots", target_name));
            return;
        }
        Triangulation.Result r = result.get();
        ChatUtils.sendLocatorResult(target_name, r);
    }

    private static void handleLocateAll(MinecraftClient client) {
        if (client.player == null) return;

        Map<String, UUID> names = LocatorState.getNamesMap();

        // String selfName = client.player.getName().getString();
        Map<String, Triangulation.Result> calculated = new HashMap<>();
        Set<String> missed = new HashSet<>();
        int found = 0;
        for (String name : names.keySet()) {
            UUID uuid = names.get(name);
            // if (name.equalsIgnoreCase(selfName)) continue;
            Optional<Triangulation.Result> result = LocatorDataCommand.getLocateSingle(uuid);

            if (result.isEmpty()) {
                missed.add(name);
                continue;
            }
            calculated.put(name, result.get());
            found++;
        }
        if (found == 0) {
            ChatUtils.sendErrorMessage("No players located (in snapshots)");
        } else {
            ChatUtils.sendLocatorResults(calculated, missed);
        }
    }
}
