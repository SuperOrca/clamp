package io.github.superorca.clamp;

import io.github.superorca.clamp.commands.*;
import io.github.superorca.clamp.listeners.ChatListener;
import io.github.superorca.clamp.listeners.ConnectListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.superorca.clamp.lib.Utils.component;

public final class Clamp extends JavaPlugin {
    private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Pattern durationPattern = Pattern.compile("(\\d+d)?(\\d+h)?(\\d+m)?(\\d+s)?");
    private Connection database;
    private boolean chatMute = false;
    private final List<UUID> staffChat = new ArrayList<>();

    @Override
    public void onEnable() {
        getDataFolder().mkdir();

        retrieveDatabase();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        try {
            database.close();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void retrieveDatabase() {
        try {
            database = DriverManager.getConnection("jdbc:sqlite:%s%sclamp.db".formatted(getDataFolder(), File.separator));
            database.setAutoCommit(false);

            Statement statement = database.createStatement();

            statement.executeUpdate(
                    """
                            CREATE TABLE IF NOT EXISTS punishments (
                                id VARCHAR(255) PRIMARY KEY,
                                date BIGINT NOT NULL,
                                type VARCHAR(255) NOT NULL,
                                target UUID NOT NULL,
                                mod UUID,
                                reason VARCHAR(255),
                                timespan BIGINT,
                                active BOOLEAN NOT NULL
                            );
                            """
            );
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private void registerCommands() {
        BukkitCommandHandler cm = BukkitCommandHandler.create(this);

        cm.registerValueResolver(Duration.class, (context) -> {
            String value = context.pop();

            Matcher matcher = durationPattern.matcher(value);
            int days = 0, hours = 0, minutes = 0, seconds = 0;

            if (matcher.matches()) {
                String daysString = matcher.group(1);
                String hoursString = matcher.group(2);
                String minutesString = matcher.group(3);
                String secondsString = matcher.group(4);

                if (daysString != null && !daysString.isEmpty()) {
                    days = Integer.parseInt(daysString.replace("d", ""));
                }
                if (hoursString != null && !hoursString.isEmpty()) {
                    hours = Integer.parseInt(hoursString.replace("h", ""));
                }
                if (minutesString != null && !minutesString.isEmpty()) {
                    minutes = Integer.parseInt(minutesString.replace("m", ""));
                }
                if (secondsString != null && !secondsString.isEmpty()) {
                    seconds = Integer.parseInt(secondsString.replace("s", ""));
                }
            } else {
                ((BukkitCommandActor) context.actor()).reply(component("<red>Invalid duration: %s".formatted(value)));
                return null;
            }

            return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        });

        cm.register(
                new BanCommand(this),
                new ClearChatCommand(this),
                new KickCommand(this),
                new MuteChatCommand(this),
                new MuteCommand(this),
                new StaffChatCommand(this),
                new TempBanCommand(this),
                new TempMuteCommand(this),
                new UnBanCommand(this),
                new UnMuteCommand(this)
        );
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new ConnectListener(this), this);
    }

    public String generateId() {
        String id;
        boolean exists;

        do {
            StringBuilder builder = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 6; i++) builder.append(characters.charAt(random.nextInt(characters.length())));

            id = builder.toString();

            try {
                PreparedStatement statement = database.prepareStatement("SELECT COUNT(*) FROM punishments WHERE id = ?");
                statement.setString(1, id);
                ResultSet result = statement.executeQuery();
                result.next();
                exists = result.getInt(1) > 0;
            } catch (SQLException e) {
                getLogger().severe(e.getMessage());
                exists = true;
            }
        } while (exists);

        return id;
    }

    public Connection getDatabase() {
        return database;
    }

    public boolean isChatMute() {
        return chatMute;
    }

    public void setChatMute(boolean mute) {
        chatMute = mute;
    }

    public List<UUID> getStaffChat() {
        return staffChat;
    }
}
