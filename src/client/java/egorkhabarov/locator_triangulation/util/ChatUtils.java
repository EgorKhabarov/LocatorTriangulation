package egorkhabarov.locator_triangulation.util;

import egorkhabarov.locator_triangulation.data_providers.Name;
import egorkhabarov.locator_triangulation.state.LocatorInfo;
import egorkhabarov.locator_triangulation.state.PlayerInfo;
import egorkhabarov.locator_triangulation.state.TargetInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.*;

public class ChatUtils {
    private static final Text prefix = Text.literal("[")
            .append(Text.literal("Locator").formatted(Formatting.YELLOW))
            .append(Text.literal("] "));
    private static final Formatting accentColor = Formatting.YELLOW;
    private static final Formatting bgColor = Formatting.GRAY;
    private static final Formatting accentBgColor = Formatting.DARK_GRAY;

    /**
     * (%.0f, %.0f) yaw=%.1f°
     */
    public static Text formatPosition(double x, double z, double yaw) {
        MutableText text = Text.empty();
        text.append(Text.literal("(").formatted(ChatUtils.bgColor));
        text.append(Text.literal(String.format("%.0f", x)).formatted(ChatUtils.accentColor));
        text.append(Text.literal(", ").formatted(ChatUtils.bgColor));
        text.append(Text.literal(String.format("%.0f", z)).formatted(ChatUtils.accentColor));
        text.append(Text.literal(") yaw").formatted(ChatUtils.bgColor));
        text.append(Text.literal("=").formatted(ChatUtils.accentBgColor));
        text.append(Text.literal(String.format("%.1f°", yaw)).formatted(ChatUtils.accentColor));
        return text;
    }

    public static void sendModMessage(Object... parts) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
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
                .withFormatting(ChatUtils.bgColor, Formatting.UNDERLINE)
                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to copy coordinates")))
                .withClickEvent(new ClickEvent.CopyToClipboard(coordsRaw))
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

    public static void sendLocatorResult(Name name, Triangulation.Result result) {
        Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.angle());
        ChatUtils.sendModMessage(
            Text.literal(name.name())
                .styled(
                    style -> style
                        .withColor(name.color())
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("UUID:" + name.uuid())))
                        .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
                ),
            ": ",
            formattedPlayerCoordinates
        );
    }

    public static void sendLocatorResults(Map<Name, Triangulation.Result> calculated, Set<Name> missed) {
        Set<Name> unionNames = new HashSet<>(calculated.keySet());
        unionNames.addAll(missed);

        MutableText headline = Text.literal("Found: " + unionNames.size())
            .formatted(ChatUtils.bgColor);
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
        for (Name name : SortUtils.sortByName(calculated.keySet(), Name::name)) {
            if (!first) {
                calculated_text.append("\n");
            }
            Triangulation.Result result = calculated.get(name);
            Text formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(
                result.x(),
                result.z(),
                result.angle()
            );
            calculated_text
                .append(
                    Text.literal(name.name())
                    .styled(
                        style -> style
                            .withColor(name.color())
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("UUID:" + name.uuid())))
                            .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
                    )
                )
                .append(": ")
                .append(formattedPlayerCoordinates);
            first = false;
        }
        MutableText missed_text = Text.empty();
        for (Name name : SortUtils.sortByName(missed, Name::name)) {
            if (!first) {
                missed_text.append("\n");
            }
            missed_text.append(
                Text.literal(name.name())
                    .styled(
                        style -> style
                            .withFormatting(Formatting.RED, Formatting.ITALIC)
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("UUID:" + name.uuid())))
                            .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
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
        if (pos == null) {
            return Text.literal("No locator data available").formatted(Formatting.RED);
        }
        MutableText text = Text.empty();
        text.append(ChatUtils.formatPosition(pos.self().x(), pos.self().z(), pos.self().yaw())).append("\n");

        if (!pos.targets().isEmpty()) {
            for (TargetInfo target : SortUtils.sortByName(pos.targets().values(), TargetInfo::name)) {
                if (target == null) {
                    continue;
                }
                text.append(
                    Text.literal(target.name())
                        .styled(
                        style -> style
                            .withColor(target.color())
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("UUID:" + target.uuid())))
                        )
                    )
                    .append(Text.literal(" yaw").formatted(ChatUtils.bgColor))
                    .append(Text.literal("=").formatted(ChatUtils.accentBgColor))
                    .append(Text.literal(String.format("%.1f°", target.yaw())).formatted(ChatUtils.accentColor))
                    .append(Text.literal(" dist").formatted(ChatUtils.bgColor))
                    .append(Text.literal("=").formatted(ChatUtils.accentBgColor))
                    .append(Text.literal(String.format("%.0f", target.distance())).formatted(ChatUtils.accentColor))
                    .append("\n");
            }
        } else {
            text.append(Text.literal("No locator data").formatted(Formatting.RED));
        }
        return text;
    }

    public static Text formatTriangulationPosition(PlayerInfo pos) {
        if (pos == null) {
            return Text.literal("No data available").formatted(Formatting.RED);
        }
        return ChatUtils.formatPosition(pos.x(), pos.z(), pos.yaw());
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
