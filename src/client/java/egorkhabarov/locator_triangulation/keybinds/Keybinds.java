package egorkhabarov.locator_triangulation.keybinds;

import egorkhabarov.locator_triangulation.command.LocatorDataCommand;
import egorkhabarov.locator_triangulation.data_providers.LocatorDataProvider;
import egorkhabarov.locator_triangulation.model.LocatorInfo;
import egorkhabarov.locator_triangulation.state.LocatorState;
import egorkhabarov.locator_triangulation.util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding setPosKey;
    private static boolean wasPressed = false;
    public static boolean last_first_position = false;

    public static void register() {
        Keybinds.setPosKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.locator_triangulation.set_pos",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.locator_triangulation"
        ));

        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            if (client.player == null) {
                return;
            }
            boolean isPressed = Keybinds.setPosKey.isPressed();

            if (Keybinds.wasPressed && !isPressed) {
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
                    Keybinds.onCtrlRelease(client);
                } else if (shiftDown) {
                    Keybinds.onShiftRelease(client);
                } else {
                    Keybinds.onRelease(client);
                }
            }

            Keybinds.wasPressed = isPressed;
        });
    }

    private static void onRelease(MinecraftClient client) {
        LocatorInfo info = LocatorDataProvider.getLocatorInfo(client);
        if (info == null) {
            ChatUtils.sendErrorMessage("Failed to capture pos");
            return;
        }
        if (Keybinds.last_first_position) {
            LocatorState.setPos2(info);
            ChatUtils.sendConfirmationMessage("Locator pos2 saved");
        } else {
            LocatorState.setPos1(info);
            ChatUtils.sendConfirmationMessage("Locator pos1 saved");
        }
        Keybinds.last_first_position = !Keybinds.last_first_position;
    }

    private static void onCtrlRelease(MinecraftClient client) {
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

    private static void onShiftRelease(MinecraftClient client) {
        ChatUtils.sendConfirmationMessage("Skipped saving locator position " + (Keybinds.last_first_position?2:1));
        Keybinds.last_first_position = !Keybinds.last_first_position;
    }
}
