package io.github.superorca.clamp.commands;

import io.github.superorca.clamp.Clamp;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static io.github.superorca.clamp.lib.Utils.component;

@Command("clearchat")
@CommandPermission("clamp.clearchat")
public class ClearChatCommand {
    private final Clamp plugin;

    public ClearChatCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("clearchat")
    public void execute(CommandSender sender, @Switch("s") boolean silent) {
        Component component = component("<newline>".repeat(250));

        Bukkit.broadcast(component);

        String modDisplay = (sender instanceof Player) ? sender.getName() : "Console";

        if (silent)
            Bukkit.broadcast(component("<yellow>[S] <gold>%s <gray>cleared the chat.".formatted(modDisplay)), "clamp.staff");
        else Bukkit.broadcast(component("<gold>%s <gray>cleared the chat.".formatted(modDisplay)));
    }
}
