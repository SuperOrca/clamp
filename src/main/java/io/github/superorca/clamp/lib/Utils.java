package io.github.superorca.clamp.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {
    public static Component component(String component) {
        return MiniMessage.miniMessage().deserialize(component);
    }
}
