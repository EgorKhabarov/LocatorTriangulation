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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class Keybinds {
    public static KeyBinding setLocatorPosKey;
    private static boolean wasLocatorPosKeyPressed = false;
    public static boolean last_first_locator_position = false;
    public static KeyBinding setTriangulationPosKey;
    private static boolean wasTriangulationPosKeyPressed = false;
    public static boolean last_first_triangulation_position = false;
    private static final KeyBinding.Category LOCATOR_TRIANGULATION_CATEGORY = KeyBinding.Category.create(Identifier.of("locator_triangulation", "category_name"));

    public static void register() {
        Keybinds.setLocatorPosKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.locator_triangulation.set_locator_pos",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            LOCATOR_TRIANGULATION_CATEGORY
        ));
        Keybinds.setTriangulationPosKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.locator_triangulation.set_triangulation_pos",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            LOCATOR_TRIANGULATION_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            if (client.player == null) {
                return;
            }
            boolean isLocatorPosKeyPressed = Keybinds.setLocatorPosKey.isPressed();
            boolean isTriangulationPosKeyPressed = Keybinds.setTriangulationPosKey.isPressed();

            if (Keybinds.wasLocatorPosKeyPressed && !isLocatorPosKeyPressed) {
                long windowHandle = client.getWindow().getHandle();
                boolean shiftDown = (
                    GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
                );
                boolean ctrlDown = (
                    GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS
                );

                if (ctrlDown) {
                    Keybinds.onLocatorPosKeyCtrlRelease(client);
                } else if (shiftDown) {
                    Keybinds.onLocatorPosKeyShiftRelease(client);
                } else {
                    Keybinds.onLocatorPosKeyRelease(client);
                }
            }

            if (Keybinds.wasTriangulationPosKeyPressed && !isTriangulationPosKeyPressed) {
                long windowHandle = client.getWindow().getHandle();
                boolean shiftDown = (
                    GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
                );
                boolean ctrlDown = (
                    GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS
                );

                if (ctrlDown) {
                    Keybinds.onTriangulationPosKeyCtrlRelease(client);
                } else if (shiftDown) {
                    Keybinds.onTriangulationPosKeyShiftRelease(client);
                } else {
                    Keybinds.onTriangulationPosKeyRelease(client);
                }
            }

            Keybinds.wasLocatorPosKeyPressed = isLocatorPosKeyPressed;
            Keybinds.wasTriangulationPosKeyPressed = isTriangulationPosKeyPressed;
        });
    }

    private static void onLocatorPosKeyRelease(MinecraftClient client) {
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

    private static void onLocatorPosKeyCtrlRelease(MinecraftClient client) {
        if (
            LocatorState.getPos1() == null
            || LocatorState.getPos2() == null
        ) {
            ChatUtils.sendModMessage(
                Text.literal("To get the result, both positions are needed")
                    .formatted(Formatting.ITALIC)
            );
            return;
        }
        LocatorDataCommand.handleLocateAll();
    }

    private static void onLocatorPosKeyShiftRelease(MinecraftClient client) {
        ChatUtils.sendConfirmationMessage("Skipped saving locator position " + (Keybinds.last_first_locator_position ?2:1));
        Keybinds.last_first_locator_position = !Keybinds.last_first_locator_position;
    }


    private static void onTriangulationPosKeyRelease(MinecraftClient client) {
        PlayerInfo playerInfo = PlayerDataProvider.getPlayerInfo(client);
        if (playerInfo == null) {
            ChatUtils.sendErrorMessage("Failed to capture pos1");
            return;
        }
        if (Keybinds.last_first_triangulation_position) {
            TriangulationState.setPos2(playerInfo);
            ChatUtils.sendConfirmationMessage("Triangulation pos2 saved");
        } else {
            TriangulationState.setPos1(playerInfo);
            ChatUtils.sendConfirmationMessage("Triangulation pos1 saved");
        }
        Keybinds.last_first_triangulation_position = !Keybinds.last_first_triangulation_position;
    }

    private static void onTriangulationPosKeyCtrlRelease(MinecraftClient client) {
        PlayerInfo pos1 = TriangulationState.getPos1();
        PlayerInfo pos2 = TriangulationState.getPos2();
        if (pos1 == null || pos2 == null) {
            ChatUtils.sendModMessage(
                Text.literal("To get the result, both positions are needed")
                    .formatted(Formatting.ITALIC)
            );
            return;
        }
        Optional<Triangulation.Result> result = Triangulation.triangulate(pos1, pos2);
        if (result.isEmpty()) {
            return;
        }
        ChatUtils.sendTriangulationResult(result.get());
    }

    private static void onTriangulationPosKeyShiftRelease(MinecraftClient client) {
        ChatUtils.sendConfirmationMessage("Skipped saving triangulation position " + (Keybinds.last_first_triangulation_position ?2:1));
        Keybinds.last_first_triangulation_position = !Keybinds.last_first_triangulation_position;
    }
}
