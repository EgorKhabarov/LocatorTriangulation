package egorkhabarov.locator_triangulation.keybinds;

import egorkhabarov.locator_triangulation.command.LocatorDataCommand;
import egorkhabarov.locator_triangulation.data_providers.LocatorDataProvider;
import egorkhabarov.locator_triangulation.data_providers.PlayerDataProvider;
import egorkhabarov.locator_triangulation.logic.Triangulation;
import egorkhabarov.locator_triangulation.model.LocatorInfo;
import egorkhabarov.locator_triangulation.model.PlayerInfo;
import egorkhabarov.locator_triangulation.state.LocatorState;
import egorkhabarov.locator_triangulation.state.TriangulationState;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class Keybinds {

    public static KeyMapping setLocatorPosKey;
    private static boolean wasLocatorPosKeyPressed = false;
    public static boolean last_first_locator_position  = false;

    public static KeyMapping setTriangulationPosKey;
    private static boolean wasTriangulationPosKeyPressed = false;
    public static boolean last_first_triangulation_position = false;

    private static final KeyMapping.Category LOCATOR_TRIANGULATION_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("category_name", "locator_triangulation"));

    public static void register() {
        setLocatorPosKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locator_triangulation.set_locator_pos",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                LOCATOR_TRIANGULATION_CATEGORY
        ));

        setTriangulationPosKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locator_triangulation.set_triangulation_pos",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                LOCATOR_TRIANGULATION_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            boolean isLocatorPressed = setLocatorPosKey.isDown();
            boolean isTriangulationPressed = setTriangulationPosKey.isDown();

            if (wasLocatorPosKeyPressed && !isLocatorPressed) {
                handleLocatorRelease(client);
            }

            if (wasTriangulationPosKeyPressed && !isTriangulationPressed) {
                handleTriangulationRelease(client);
            }

            wasLocatorPosKeyPressed = isLocatorPressed;
            wasTriangulationPosKeyPressed = isTriangulationPressed;
        });
    }

    private static void handleLocatorRelease(Minecraft client) {
        long window = client.getWindow().handle();

        boolean shiftDown = isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
        boolean ctrlDown = isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);

        if (ctrlDown) {
            Keybinds.onLocatorPosKeyCtrlRelease(client);
        } else if (shiftDown) {
            Keybinds.onLocatorPosKeyShiftRelease(client);
        } else {
            Keybinds.onLocatorPosKeyRelease(client);
        }
    }

    private static void handleTriangulationRelease(Minecraft client) {
        long window = client.getWindow().handle();

        boolean shift = isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
        boolean ctrl = isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);

        if (ctrl) {
            onTriangulationPosKeyCtrlRelease(client);
        } else if (shift) {
            onTriangulationPosKeyShiftRelease(client);
        } else {
            onTriangulationPosKeyRelease(client);
        }
    }

    private static boolean isKeyDown(long window, int... keys) {
        for (int key : keys) {
            if (GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
                return true;
            }
        }
        return false;
    }

    private static void onLocatorPosKeyRelease(Minecraft client) {
        LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
        if (info == null) {
            ChatUtils.sendErrorMessage("Failed to capture pos");
            return;
        }

        if (Keybinds.last_first_locator_position) {
            LocatorState.setPos2(info);
            ChatUtils.sendConfirmationMessage("Locator pos2 saved");
        } else {
            LocatorState.setPos1(info);
            ChatUtils.sendConfirmationMessage("Locator pos1 saved");
        }
        Keybinds.last_first_locator_position = !Keybinds.last_first_locator_position;
    }

    private static void onLocatorPosKeyCtrlRelease(Minecraft client) {
        if (LocatorState.getPos1() == null || LocatorState.getPos2() == null) {
            ChatUtils.sendModMessage(
                    Component.literal("To get the result, both positions are needed")
                            .withStyle(ChatFormatting.ITALIC)
            );
            return;
        }
        LocatorDataCommand.handleLocateAll();
    }

    private static void onLocatorPosKeyShiftRelease(Minecraft client) {
        ChatUtils.sendConfirmationMessage("Skipped saving locator position " + (Keybinds.last_first_locator_position ?2:1));
        Keybinds.last_first_locator_position = !Keybinds.last_first_locator_position;
    }

    private static void onTriangulationPosKeyRelease(Minecraft client) {
        PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
        if (playerInfo == null) {
            ChatUtils.sendErrorMessage("Failed to capture pos1");
            return;
        }
        if (last_first_triangulation_position) {
            TriangulationState.setPos2(playerInfo);
            ChatUtils.sendConfirmationMessage("Triangulation pos2 saved");
        } else {
            TriangulationState.setPos1(playerInfo);
            ChatUtils.sendConfirmationMessage("Triangulation pos1 saved");
        }
        last_first_triangulation_position = !last_first_triangulation_position;
    }

    private static void onTriangulationPosKeyCtrlRelease(Minecraft client) {
        PlayerInfo pos1 = TriangulationState.getPos1();
        PlayerInfo pos2 = TriangulationState.getPos2();

        if (pos1 == null || pos2 == null) {
            ChatUtils.sendModMessage(
                    Component.literal("To get the result, both positions are needed")
                            .withStyle(ChatFormatting.ITALIC)
            );
            return;
        }
        Optional<Triangulation.Result> result = Triangulation.triangulate(pos1, pos2);
        if (result.isEmpty()) {
            return;
        }
        ChatUtils.sendTriangulationResult(result.get());
    }

    private static void onTriangulationPosKeyShiftRelease(Minecraft client) {
        ChatUtils.sendConfirmationMessage(
                "Skipped saving triangulation position " +
                        (last_first_triangulation_position ? 2 : 1)
        );
        last_first_triangulation_position = !last_first_triangulation_position;
    }
}
