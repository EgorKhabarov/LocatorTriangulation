package egorkhabarov.locator_triangulation.command;

import egorkhabarov.locator_triangulation.data_providers.PlayerDataProvider;
import egorkhabarov.locator_triangulation.state.PlayerInfo;
import egorkhabarov.locator_triangulation.state.TriangulationState;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import egorkhabarov.locator_triangulation.util.Triangulation;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class TriangulationCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("triangulation_pos1")
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

            dispatcher.register(ClientCommandManager.literal("triangulation_pos2")
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

            dispatcher.register(ClientCommandManager.literal("triangulation_get_poses")
                .executes(context -> {
                    ChatUtils.sendTriangulationPositions(TriangulationState.getPos1(), TriangulationState.getPos2());
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("triangulation_clear_pos1")
                .executes(context -> {
                    TriangulationState.clearPos1();
                    ChatUtils.sendConfirmationMessage("pos1 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("triangulation_clear_pos2")
                .executes(context -> {
                    TriangulationState.clearPos2();
                    ChatUtils.sendConfirmationMessage("pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("triangulation_clear_poses")
                .executes(context -> {
                    TriangulationState.clearAll();
                    ChatUtils.sendConfirmationMessage("pos1 and pos2 cleared");
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("triangulation_locate")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) return 1;

                    PlayerInfo pos1 = TriangulationState.getPos1();
                    PlayerInfo pos2 = TriangulationState.getPos2();

                    if (pos1 == null || pos2 == null) {
                        ChatUtils.sendErrorMessage("Need both pos1 and pos2");
                    }

                    Optional<Triangulation.Result> result = Triangulation.triangulate(pos1, pos2);

                    if (result.isEmpty()) return 1;

                    ChatUtils.sendTriangulationResult(result.get());
                    return 1;
                })
            );
        });
    }
}
