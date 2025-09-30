package egorkhabarov.trigonolocator.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.UUID;

public class ChatUtils {


    public static void sendModMessage(String message) {
        Text prefix = Text.literal("[")
                .append(Text.literal("Locator").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF55))))
                .append(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF))));

        /*Text msg = prefix.copy()
                // .append(playerNameText)
                .append(message)

        client.player.sendMessage(msg, false);*/
    }

    public static void sendClientMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), false);
        }
    }

    /**
     * Вывод результата локализации в чат.
     * При клике на "(x, z)" копируется "x, z" (без скобок).
     */
    public static void sendLocatorResult(/*UUID uuid, */String name, double x, double z, double error) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Префикс: [ Locator ] — скобки белые, "Locator" жёлтый
        Text prefix = Text.literal("[")
                .append(Text.literal("Locator").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF55))))
                .append(Text.literal("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF))));

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

        // coordsRaw — то, что должно копироваться: "123.456, -987.654"
        String coordsRaw = String.format("%.0f, %.0f", x, z);

        // Text для координат: визуально "(123.456, -987.654)",
        // но с кликом копируется coordsRaw и есть hover-подсказка.
        Text coords = Text.literal(coordsRaw)
                .styled(style -> style
                        .withColor(TextColor.fromRgb(0xFFFFFF))
                        .withClickEvent(new ClickEvent.CopyToClipboard(String.format("%.0f %.0f", x, z)))
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to copy coordinates")))
                );

        // Дистанция — серым
        Text distance = Text.literal(String.format(" ~%.1fm", error))
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAAAA)));

        Text msg = prefix.copy()
                // .append(playerNameText)
                .append(name)
                .append(": ")
                .append(coords)
                .append(distance);

        client.player.sendMessage(msg, false);
    }
}
