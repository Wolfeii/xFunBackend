package se.xfunserver.xfunbackend.proxy.assets.listeners;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import se.xfunserver.xfunbackend.assets.xFunLogger;

public class ServerConnectListener implements Listener {

    @EventHandler
    public void onPluginMessage(ServerConnectEvent event) {
        xFunLogger.debug(event.getReason().toString());
    }
}
