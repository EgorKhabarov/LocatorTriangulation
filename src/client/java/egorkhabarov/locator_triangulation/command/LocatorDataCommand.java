package egorkhabarov.locator_triangulation.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import egorkhabarov.locator_triangulation.data_providers.Name;
import egorkhabarov.locator_triangulation.state.*;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
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
                    ChatUtils.sendLocatorPositions(LocatorDataProvider.getLocatorInfo(client));
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
                        ChatUtils.sendConfirmationMessage("Locator pos1 saved");
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
                        ChatUtils.sendConfirmationMessage("Locator pos2 saved");
                    }
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("locator_get_poses")
                .executes(context -> {
                    ChatUtils.sendLocatorPositions(LocatorState.getPos1(), LocatorState.getPos2());
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("locator_clear_pos1")
                .executes(context -> {
                    LocatorState.clearPos1();
                    ChatUtils.sendConfirmationMessage("pos1 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("locator_clear_pos2")
                .executes(context -> {
                    LocatorState.clearPos2();
                    ChatUtils.sendConfirmationMessage("pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("locator_clear_poses")
                .executes(context -> {
                    LocatorState.clearAll();
                    ChatUtils.sendConfirmationMessage("pos1 and pos2 cleared");
                    return 1;
                })
            );

            // locate <player_name>
            dispatcher.register(
                ClientCommandManager.literal("locator_locate")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            Set<String> names = new HashSet<>(LocatorState.getNamesMap().keySet());
                            if (client.world != null) {
                                for (PlayerEntity p : client.world.getPlayers()) {
                                    names.add(p.getGameProfile().name());
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
                            if (client.player == null) {
                                return 1;
                            }
                            if (
                                LocatorState.getPos1() == null
                                && LocatorState.getPos2() == null
                            ) {
                                ChatUtils.sendErrorMessage("Need both pos1 and pos2");
                                return 1;
                            }
                            String target_name = StringArgumentType.getString(context, "player");
                            Name name = LocatorState.getNamesMap().get(target_name);
                            if (name == null) {
                                ChatUtils.sendErrorMessage(String.format("Player \"%s\" not found in saved snapshots", target_name));
                                return 1;
                            }
                            LocatorDataCommand.handleLocateSingle(name);
                            return 1;
                        })
                    )
            );

            dispatcher.register(ClientCommandManager.literal("locator_locate_all")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) {
                        return 1;
                    }
                    if (
                        LocatorState.getPos1() == null
                        && LocatorState.getPos2() == null
                    ) {
                        ChatUtils.sendErrorMessage("Need both pos1 and pos2");
                        return 1;
                    }
                    LocatorDataCommand.handleLocateAll();
                    return 1;
                })
            );
        });
    }

    private static Optional<Triangulation.Result> getResultSingle(UUID uuid) {
        LocatorInfo pos1 = LocatorState.getPos1();
        LocatorInfo pos2 = LocatorState.getPos2();

        if (pos1 == null || pos2 == null) {
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

    private static void handleLocateSingle(Name name) {
        Optional<Triangulation.Result> result = LocatorDataCommand.getResultSingle(name.uuid());
        if (result.isEmpty()) {
            ChatUtils.sendErrorMessage("Something went wrong");
            return;
        }
        ChatUtils.sendLocatorResult(name, result.get());
    }

    private static void handleLocateAll() {
        Map<String, Name> names = LocatorState.getNamesMap();

        Map<Name, Triangulation.Result> calculated = new HashMap<>();
        Set<Name> missed = new HashSet<>();
        int found = 0;

        for (Name name : names.values()) {
            Optional<Triangulation.Result> result = LocatorDataCommand.getResultSingle(name.uuid());

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
