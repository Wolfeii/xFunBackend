package se.xfunserver.xfunbackend.proxy.user.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;

import java.util.List;
import java.util.UUID;

public class UserQuitProxyListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerDisconnectEvent e) {
        ProxyUser proxyUser = Core.getCore().getUserManager().getUser(e.getPlayer().getUniqueId());

        Core.getCore().getFriendManager().updateFriendsForUser(proxyUser);
        Core.getCore().getUserManager().getProxyUsers().remove(proxyUser.getUuid());
    }
}
