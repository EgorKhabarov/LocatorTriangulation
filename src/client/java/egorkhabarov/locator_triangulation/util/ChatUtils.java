package egorkhabarov.locator_triangulation.util;

import egorkhabarov.locator_triangulation.logic.Triangulation;
import egorkhabarov.locator_triangulation.model.Name;
import egorkhabarov.locator_triangulation.model.LocatorInfo;
import egorkhabarov.locator_triangulation.model.PlayerInfo;
import egorkhabarov.locator_triangulation.model.TargetInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.ChatFormatting;

import java.util.*;

public class ChatUtils {
    private static final Component prefix = Component.literal("[")
            .append(Component.literal("Locator").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("] "));
    private static final ChatFormatting accentColor = ChatFormatting.YELLOW;
    private static final ChatFormatting bgColor = ChatFormatting.GRAY;
    private static final ChatFormatting accentBgColor = ChatFormatting.DARK_GRAY;

    /**
     * (%.0f, %.0f) yaw=%.1f°
     */
    public static Component formatPosition(double x, double z, double yaw) {
        MutableComponent text = Component.empty();
        text.append(Component.literal("(").withStyle(ChatUtils.bgColor));
        text.append(Component.literal(String.format("%.0f", x)).withStyle(ChatUtils.accentColor));
        text.append(Component.literal(", ").withStyle(ChatUtils.bgColor));
        text.append(Component.literal(String.format("%.0f", z)).withStyle(ChatUtils.accentColor));
        text.append(Component.literal(") yaw").withStyle(ChatUtils.bgColor));
        text.append(Component.literal("=").withStyle(ChatUtils.accentBgColor));
        text.append(Component.literal(String.format("%.1f°", yaw)).withStyle(ChatUtils.accentColor));
        return text;
    }

    public static void sendModMessage(Object... parts) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        MutableComponent result = ChatUtils.prefix.copy();

        for (Object part : parts) {
            if (part instanceof Component text) {
                result.append(text);
            } else if (part instanceof String str) {
                result.append(Component.literal(str));
            } else {
                result.append(Component.literal(String.valueOf(part)));
            }
        }
        client.player.sendSystemMessage(result);
    }

    public static void sendErrorMessage(String message) {
        ChatUtils.sendModMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }

    public static void sendConfirmationMessage(String message) {
        ChatUtils.sendModMessage(Component.literal(message).withStyle(ChatFormatting.GREEN));
    }

    public static Component formatPlayerCoordinates(double x, double z, double angle) {
        String coordsRaw = String.format("%.0f %.0f", x, z);
        MutableComponent coords = Component.literal(coordsRaw)
            .withStyle(style -> style
                .withColor(ChatUtils.bgColor)
                .withUnderlined(true)
                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to copy coordinates")))
                .withClickEvent(new ClickEvent.CopyToClipboard(coordsRaw))
            );

        ChatFormatting angle_color;
        if (angle >= 70 && angle <= 100)
            angle_color = ChatFormatting.GREEN;
        else if (angle >= 20 && angle < 70 || angle > 100 && angle <= 150)
            angle_color = ChatFormatting.YELLOW;
        else
            angle_color = ChatFormatting.RED;

        MutableComponent angleText = Component.literal(String.format(" %.1f°", angle))
            .setStyle(Style.EMPTY.withColor(angle_color));

        return Component.empty()
            .append(coords)
            .append(angleText);
    }

    public static void sendLocatorResult(Name name, Triangulation.Result result) {
        Component formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.angle());
        ChatUtils.sendModMessage(
            Component.literal(name.name())
                .withStyle(
                    style -> style
                        .withColor(name.color())
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("UUID:" + name.uuid())))
                        .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
                ),
            ": ",
            formattedPlayerCoordinates
        );
    }

    public static void sendLocatorResults(Map<Name, Triangulation.Result> calculated, Set<Name> missed) {
        Set<Name> unionNames = new HashSet<>(calculated.keySet());
        unionNames.addAll(missed);

        MutableComponent headline = Component.literal("Found: " + unionNames.size())
            .withStyle(ChatUtils.bgColor);
        if (!calculated.isEmpty()) {
            headline.append(
                Component.literal(" Calculated: " + calculated.size())
                    .withStyle(ChatFormatting.GREEN)
            );
        }
        if (!missed.isEmpty()) {
            headline.append(
                Component.literal(" Missed: " + missed.size())
                    .withStyle(ChatFormatting.RED)
            );
        }

        MutableComponent calculated_text = Component.empty();
        boolean first = true;
        for (Name name : SortUtils.sortByName(calculated.keySet(), Name::name)) {
            if (!first) {
                calculated_text.append("\n");
            }
            Triangulation.Result result = calculated.get(name);
            Component formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(
                result.x(),
                result.z(),
                result.angle()
            );
            calculated_text
                .append(
                    Component.literal(name.name())
                    .withStyle(
                        style -> style
                            .withColor(name.color())
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("UUID:" + name.uuid())))
                            .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
                    )
                )
                .append(": ")
                .append(formattedPlayerCoordinates);
            first = false;
        }
        MutableComponent missed_text = Component.empty();
        for (Name name : SortUtils.sortByName(missed, Name::name)) {
            if (!first) {
                missed_text.append("\n");
            }
            missed_text.append(
                Component.literal(name.name())
                    .withStyle(
                        style -> style
                            .withColor(ChatFormatting.RED)
                            .withItalic(true)
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("UUID:" + name.uuid())))
                            .withClickEvent(new ClickEvent.CopyToClipboard(name.uuid().toString()))
                    )
            );
            first = false;
        }

        ChatUtils.sendModMessage(
            Component.empty()
                .append(headline)
                .append("\n")
                .append(calculated_text)
                .append(missed_text)
        );
    }

    public static void sendTriangulationResult(Triangulation.Result result) {
        Component formattedPlayerCoordinates = ChatUtils.formatPlayerCoordinates(result.x(), result.z(), result.angle());
        ChatUtils.sendModMessage(formattedPlayerCoordinates);
    }

    public static Component formatLocatorPosition(LocatorInfo pos) {
        if (pos == null) {
            return Component.literal("No locator data available").withStyle(ChatFormatting.RED);
        }
        MutableComponent text = Component.empty();
        text.append(ChatUtils.formatPosition(pos.self().x(), pos.self().z(), pos.self().yaw())).append("\n");

        if (!pos.targets().isEmpty()) {
            for (TargetInfo target : SortUtils.sortByName(pos.targets().values(), TargetInfo::name)) {
                if (target == null) {
                    continue;
                }
                text.append(
                    Component.literal(target.name())
                        .withStyle(
                        style -> style
                            .withColor(target.color())
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("UUID:" + target.uuid())))
                        )
                    )
                    .append(Component.literal(" yaw").withStyle(ChatUtils.bgColor))
                    .append(Component.literal("=").withStyle(ChatUtils.accentBgColor))
                    .append(Component.literal(String.format("%.1f°", target.yaw())).withStyle(ChatUtils.accentColor))
                    .append(Component.literal(" dist").withStyle(ChatUtils.bgColor))
                    .append(Component.literal("=").withStyle(ChatUtils.accentBgColor))
                    .append(Component.literal(String.format("%.0f", target.distance())).withStyle(ChatUtils.accentColor))
                    .append("\n");
            }
        } else {
            text.append(Component.literal("No locator data").withStyle(ChatFormatting.RED));
        }
        return text;
    }

    public static Component formatTriangulationPosition(PlayerInfo pos) {
        if (pos == null) {
            return Component.literal("No data available").withStyle(ChatFormatting.RED);
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
        MutableComponent text = Component.empty();
        for (LocatorInfo pos : positions) {
            Component formatLocatorPosition = ChatUtils.formatLocatorPosition(pos);
            text.append(Component.literal("Pos " + counter + "\n").withStyle(ChatFormatting.BOLD))
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
        MutableComponent text = Component.empty();
        for (PlayerInfo pos : positions) {
            Component formatTriangulationPosition = ChatUtils.formatTriangulationPosition(pos);
            text.append(Component.literal("Pos " + counter + "\n").withStyle(ChatFormatting.BOLD))
                .append(formatTriangulationPosition).append("\n");
            counter++;
        }
        ChatUtils.sendModMessage(text);
    }
}
