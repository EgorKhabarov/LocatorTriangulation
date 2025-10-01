package egorkhabarov.locator_triangulation.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class ChatUtils {
    static Text prefix = Text.literal("[")
            .append(Text.literal("Locator").formatted(Formatting.YELLOW))
            .append(Text.literal("] "));

    public static void sendModMessage(Object... parts) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        MutableText result = ChatUtils.prefix.copy();

        for (Object part : parts) {
            if (part instanceof Text text) {
                result.append(text);
            } else if (part instanceof String str) {
                result.append(Text.literal(str));
            } else {
                result.append(Text.literal(String.valueOf(part)));
            }
        }
        client.player.sendMessage(result, false);
    }

    public static void sendInfoMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.GRAY));
    }

    public static void sendErrorMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.RED));
    }

    public static void sendConfirmationMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.GREEN));
    }

    public static Text formatPlayerCoordinates(double x, double z, double distance) {
        String coordsRaw = String.format("%.0f %.0f", x, z);
        return Text.literal(coordsRaw)
            .styled(style -> style
                .withColor(Formatting.UNDERLINE)
                .withColor(Formatting.GRAY)
                .withClickEvent(new ClickEvent.CopyToClipboard(coordsRaw))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to copy coordinates")))
            ).append(
                Text.literal(String.format(" ~%.0fm", distance))
                    .formatted(Formatting.DARK_GRAY)
            );
    }

    public static void sendLocatorResult(/*UUID uuid, */String name, double x, double z, double error) {
        // Ник игрока: если в табе есть форматированный displayName (с цветом), используем его,
        // иначе — просто plain белый текст
        /*Text playerNameText;
        PlayerListEntry entry = (uuid != null && client.getNetworkHandler() != null)
                ? client.getNetworkHandler().getPlayerListEntry(uuid)
                : null;

        if (entry != null && entry.getDisplayName() != null) {
            playerNameText = entry.getDisplayName();
        } else {
            playerNameText = Text.literal(name).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)));
        }*/
        // String playerNameText = name;
        Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(x, z, error);
        ChatUtils.sendModMessage(name, ": ", formattedPlayerCoordinates);
    }
}
