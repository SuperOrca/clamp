package io.github.superorca.clamp.lib.punishments;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.Date;

import static io.github.superorca.clamp.lib.Utils.component;

public enum PunishmentType {
    BAN,
    MUTE,
    KICK;

    public Component getDisplay(OfflinePlayer target, OfflinePlayer mod, String reason, Duration timespan) {
        String targetName = target.getName();
        String modName = (mod == null) ? "Console" : mod.getName();
        String reasonDisplay = (reason == null) ? "N/A" : reason;
        String timespanDisplay = (timespan == null) ? "FOREVER" : DurationFormatUtils.formatDurationWords(timespan.toMillis(), true, true).toUpperCase();

        switch (this) {
            case BAN -> {
                return component("<gold>%s <gray>was banned by <gold>%s <gray>for <white>%s<gray>. <gray>[%s]".formatted(targetName, modName, reasonDisplay, timespanDisplay));
            }

            case MUTE -> {
                return component("<gold>%s <gray>was muted by <gold>%s <gray>for <white>%s<gray>. <gray>[%s]".formatted(targetName, modName, reasonDisplay, timespanDisplay));
            }

            case KICK -> {
                return component("<gold>%s <gray>was kicked by <gold>%s <gray>for <white>%s<gray>.".formatted(targetName, modName, reasonDisplay));
            }
        }

        return null;
    }

    public Component getMessage(String id, Date date, String reason, Duration timespan) {
        String reasonDisplay = (reason == null) ? "N/A" : reason;
        String timespanDisplay = (timespan == null) ? "never" : DurationFormatUtils.formatDurationWords((date.getTime() + timespan.toMillis()) - System.currentTimeMillis(), true, true);

        switch (this) {
            case BAN -> {
                return component("<gray>You are banned for <white>%s<gray>. It will expire in <white>%s<gray>.<newline><newline>You can appeal on <white><click:open_url:'http://dsc.gg/amberpr'>dsc.gg/amberpr</click><gray>. <dark_gray>#%s<newline><dark_gray>".formatted(reasonDisplay, timespanDisplay, id));
            }

            case MUTE -> {
                return component("<dark_gray><strikethrough>--------------------<newline><reset><gray>You are muted for <white>%s<gray>. It will expire in <white>%s<gray>.<newline>You can appeal on <white><click:open_url:'http://dsc.gg/amberpr'>dsc.gg/amberpr</click><gray>. <dark_gray>#%s<newline><dark_gray><strikethrough>--------------------".formatted(reasonDisplay, timespanDisplay, id));
            }

            case KICK -> {
                return component("<gray>You were kicked for <white>%s<gray>. <dark_gray>#%s".formatted(reasonDisplay, id));
            }
        }

        return null;
    }
}
