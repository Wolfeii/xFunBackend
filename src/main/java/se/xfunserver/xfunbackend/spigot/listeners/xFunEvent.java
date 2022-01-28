package se.xfunserver.xfunbackend.spigot.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class xFunEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
