package egorkhabarov.locator_triangulation.util;

import egorkhabarov.locator_triangulation.state.LocatorInfo;
import egorkhabarov.locator_triangulation.state.PlayerInfo;
import egorkhabarov.locator_triangulation.state.TargetInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    public static void sendErrorMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.RED));
    }

    public static void sendConfirmationMessage(String message) {
        ChatUtils.sendModMessage(Text.literal(message).formatted(Formatting.GREEN));
    }

    public static Text formatPlayerCoordinates(double x, double z, double angle) {
        String coordsRaw = String.format("%.0f %.0f", x, z);
        MutableText coords = Text.literal(coordsRaw)
            .styled(style -> style
                .withFormatting(Formatting.GRAY, Formatting.UNDERLINE)
                .withClickEvent(new ClickEvent.CopyToClipboard(coordsRaw))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to copy coordinates")))
            );

        Formatting angle_color;
        if (angle >= 70 && angle <= 100)
            angle_color = Formatting.GREEN;
        else if (angle >= 20 && angle < 70 || angle > 100 && angle <= 150)
            angle_color = Formatting.YELLOW;
        else
            angle_color = Formatting.RED;

        MutableText angleText = Text.literal(String.format(" %.1f°", angle))
            .setStyle(Style.EMPTY.withColor(angle_color));

        return Text.empty()
            .append(coords)
            .append(angleText);
    }

    public static void sendLocatorResult(String name, Triangulation.Result result) {
        Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.angle());
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

        MutableText headline = Text.literal("Found: " + unionNames.size())
            .formatted(Formatting.GRAY);
        if (!calculated.isEmpty()) {
            headline.append(
                Text.literal(" Calculated: " + calculated.size())
                    .formatted(Formatting.GREEN)
            );
        }
        if (!missed.isEmpty()) {
            headline.append(
                Text.literal(" Missed: " + missed.size())
                    .formatted(Formatting.RED)
            );
        }

        MutableText calculated_text = Text.empty();
        boolean first = true;
        for (String name : calculated.keySet()) {
            if (!first) calculated_text.append("\n");
            Triangulation.Result result = calculated.get(name);
            Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(
                result.x(),
                result.z(),
                result.angle()
            );
            calculated_text.append(String.format("%" + maxLength + "s", name))
                .append(": ")
                .append(formattedPlayerCoordinates);
            first = false;
        }
        MutableText missed_text = Text.empty();
        for (String name : missed) {
            if (!first) missed_text.append("\n");
            missed_text.append(
                Text.literal(String.format("%" + maxLength + "s", name))
                    .formatted(
                        Formatting.RED,
                        Formatting.ITALIC
                    )
            );
            first = false;
        }

        ChatUtils.sendModMessage(
            Text.empty()
                .append(headline)
                .append("\n")
                .append(calculated_text)
                .append(missed_text)
        );
    }

    public static void sendTriangulationResult(Triangulation.Result result) {
        Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.angle());
        ChatUtils.sendModMessage(formattedPlayerCoordinates);
    }

    public static Text formatLocatorPosition(LocatorInfo pos) {
        if (pos == null)
            return Text.literal("No locator data available").formatted(Formatting.RED);

        MutableText text = Text.empty();
        Text headline = Text.literal(
            String.format(
                "(%.0f, %.0f) yaw=%.1f°",
                pos.self().x(),
                pos.self().z(),
                pos.self().yaw()
            )
        ).formatted(Formatting.GRAY);
        text.append(headline).append("\n");

        if (!pos.targets().keySet().isEmpty()) {
            for (UUID key : pos.targets().keySet()) {
                TargetInfo target = pos.targets().get(key);
                if (target == null) {
                    continue;
                }
                text.append(Text.literal(target.name()).formatted(Formatting.GRAY))
                    .append(Text.literal(" yaw=").formatted(Formatting.GRAY))
                    .append(Text.literal(String.format("%.1f°", target.yaw())).formatted(Formatting.YELLOW))
                    .append(Text.literal(" dist=").formatted(Formatting.GRAY))
                    .append(Text.literal(String.format("%.0f", target.distance())).formatted(Formatting.YELLOW))
                    .append("\n");
            }
        } else {
            text.append(Text.literal("No locator data").formatted(Formatting.RED));
        }
        return text;
    }

    public static Text formatTriangulationPosition(PlayerInfo pos) {
        if (pos == null)
            return Text.literal("No data available").formatted(Formatting.RED);

        return Text.literal(
            String.format(
                "(%.0f, %.0f) yaw=%.1f°",
                pos.x(),
                pos.z(),
                pos.yaw()
            )
        ).formatted(Formatting.GRAY);
    }

    public static void sendLocatorPositions(LocatorInfo... positions) {
        int count = positions.length;
        if (count == 1) {
            LocatorInfo pos = positions[0];
            ChatUtils.sendModMessage(ChatUtils.formatLocatorPosition(pos));
            return;
        }
        int counter = 1;
        MutableText text = Text.empty();
        for (LocatorInfo pos : positions) {
            Text formatLocatorPosition = ChatUtils.formatLocatorPosition(pos);
            text.append(Text.literal("Pos " + counter + "\n").formatted(Formatting.BOLD))
                .append(formatLocatorPosition).append("\n");
            counter++;
        }
        ChatUtils.sendModMessage(text);
    }

    public static void sendTriangulationPositions(PlayerInfo... positions) {
        int count = positions.length;
        if (count == 1) {
            PlayerInfo pos = positions[0];
            ChatUtils.sendModMessage(ChatUtils.formatTriangulationPosition(pos));
            return;
        }
        int counter = 1;
        MutableText text = Text.empty();
        for (PlayerInfo pos : positions) {
            Text formatTriangulationPosition = ChatUtils.formatTriangulationPosition(pos);
            text.append(Text.literal("Pos " + counter + "\n").formatted(Formatting.BOLD))
                .append(formatTriangulationPosition).append("\n");
            counter++;
        }
        ChatUtils.sendModMessage(text);
    }
}
