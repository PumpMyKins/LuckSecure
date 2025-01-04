package fr.pmk.lucksecure.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Util {
        public static Component mm(String miniMessageString) {
        return MiniMessage.miniMessage().deserialize(miniMessageString);
    } 
}
