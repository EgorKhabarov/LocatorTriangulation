package egorkhabarov.locator_triangulation.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static Text formatPlayerCoordinates(double x, double z, double error) {
        String coordsRaw = String.format("%.0f %.0f", x, z);
        return Text.literal(coordsRaw)
            .styled(style -> style
                .withColor(Formatting.UNDERLINE)
                .withColor(Formatting.GRAY)
                .withClickEvent(new ClickEvent.CopyToClipboard(coordsRaw))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to copy coordinates")))
            ).append(
                Text.literal(String.format(" ~%.2f", error))
                    .formatted(Formatting.DARK_GRAY)
            );
    }

    public static void sendLocatorResult(/*UUID uuid, */String name, Triangulation.Result result) {
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

        Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.error());
        ChatUtils.sendModMessage(name, ": ", formattedPlayerCoordinates);
    }

    public static void sendLocatorResults(Map<String, Triangulation.Result> calculated, Set<String> missed) {
        Set<String> unionNames = new HashSet<>(calculated.keySet());
        unionNames.addAll(missed);
        int maxLength = unionNames.stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        if (maxLength > 16) {
            maxLength = 16;
        }

        MutableText text = Text.literal("");
        boolean first = true;
        for (String name : calculated.keySet()) {
            if (!first) text.append("\n");
            Triangulation.Result result = calculated.get(name);
            Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.error());
            text.append(String.format("%" + maxLength + "s", name))
                .append(": ")
                .append(formattedPlayerCoordinates);
            first = false;
        }
        for (String name : missed) {
            if (!first) text.append("\n");
            text.append(
                Text.literal(name)
                    .formatted(
                        Formatting.RED,
                        Formatting.STRIKETHROUGH,
                        Formatting.ITALIC
                    )
            );
            first = false;
        }
        ChatUtils.sendModMessage(text);
    }
}
