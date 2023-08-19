package io.github.superorca.clamp.lib.punishments;

import io.github.superorca.clamp.Clamp;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.*;

import static io.github.superorca.clamp.lib.Utils.component;

public class Punishment {
    private final Clamp plugin;
    private final String id;
    private final Date date;
    private final PunishmentType type;
    private final OfflinePlayer target;
    private final OfflinePlayer mod;
    private final String reason;
    private final Duration timespan;
    private final boolean active;

    public Punishment(@NotNull Clamp plugin, @NotNull String id, @NotNull Date date, @NotNull PunishmentType type, @NotNull OfflinePlayer target, OfflinePlayer mod, String reason, Duration timespan, boolean active) {
        this.plugin = plugin;
        this.id = id;
        this.date = date;
        this.type = type;
        this.target = target;
        this.mod = mod;
        this.reason = reason;
        this.timespan = timespan;
        this.active = active;
    }

    public static Punishment from(Clamp plugin, ResultSet result) {
        try {
            String id = result.getString("id");
            Date date = new Date(result.getLong("date"));
            PunishmentType type = PunishmentType.valueOf(result.getString("type"));
            OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("target")));
            OfflinePlayer mod = (result.getObject("mod") == null) ? null : Bukkit.getOfflinePlayer(UUID.fromString(result.getString("mod")));
            String reason = (result.getObject("reason") == null) ? null : result.getString("reason");
            Duration timespan = (result.getObject("timespan") == null) ? null : Duration.ofMillis(result.getLong("timespan"));
            boolean active = result.getBoolean("active");

            return new Punishment(plugin, id, date, type, target, mod, reason, timespan, active);
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }

        return null;
    }

    public static Punishment fromId(Clamp plugin, String id) {
        try (PreparedStatement statement = plugin.getDatabase().prepareStatement("SELECT * FROM punishments WHERE id = ?")) {
            statement.setString(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) return Punishment.from(plugin, result);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }

        return null;
    }

    public static List<Punishment> fromTarget(Clamp plugin, UUID target) {
        List<Punishment> punishments = new ArrayList<>();

        try (PreparedStatement statement = plugin.getDatabase().prepareStatement("SELECT * FROM punishments WHERE target = ?")) {
            statement.setString(1, target.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) punishments.add(Punishment.from(plugin, result));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }

        return punishments;
    }

    public void write() {
        try {
            PreparedStatement statement = plugin.getDatabase().prepareStatement("UPDATE punishments SET active = false WHERE target = ? AND type = ? AND active = true");

            statement.setString(1, target.getUniqueId().toString());
            statement.setString(2, type.name());
            statement.executeUpdate();
            statement.close();

            statement = plugin.getDatabase().prepareStatement("INSERT OR REPLACE INTO punishments (id, date, type, target, mod, reason, timespan, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, id);
            statement.setLong(2, date.getTime());
            statement.setString(3, type.name());
            statement.setString(4, target.getUniqueId().toString());
            if (mod == null) statement.setNull(5, Types.VARCHAR);
            else statement.setString(5, mod.getUniqueId().toString());
            if (reason == null) statement.setNull(6, Types.VARCHAR);
            else statement.setString(6, reason);
            if (timespan == null) statement.setNull(7, Types.BIGINT);
            else statement.setLong(7, timespan.toMillis());
            statement.setBoolean(8, active);
            statement.executeUpdate();
            statement.close();

            plugin.getDatabase().commit();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public void display(boolean silent) {
        Component component = type.getDisplay(target, mod, reason, timespan);
        assert component != null;

        if (silent) {
            component = component("<yellow>[S]").append(component);

            Bukkit.broadcast(component, "clamp.staff");
        } else {
            Bukkit.broadcast(component);
        }
    }

    public Component getMessage() {
        return type.getMessage(id, date, reason, timespan);
    }

    public void execute() {
        Objects.requireNonNull(target.getPlayer()).kick(getMessage());
    }

    public void delete() {
        try (PreparedStatement statement = plugin.getDatabase().prepareStatement("UPDATE punishments SET active = false WHERE id = ?")) {
            statement.setString(1, id);

            statement.executeUpdate();
            statement.close();

            plugin.getDatabase().commit();
        } catch (SQLException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    public Date getDate() {
        return date;
    }

    public Duration getTimespan() {
        return timespan;
    }

    public PunishmentType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }
}
