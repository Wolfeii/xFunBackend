package se.xfunserver.xfunbackend.proxy.user.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserJoinProxyListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        event.registerIntent(Core.getCore());

        Core.getCore().getProxy().getScheduler().runAsync(Core.getCore(), () -> {
            List<UUID> friends = Core.getCore().getFriendManager().getFriendsForUser(event.getConnection().getUniqueId());

            Rank rank = Rank.NONE;

            try {
                ResultSet resultSet = Core.getCore().getDatabaseManager().getResults("users", "uuid=?",
                        new HashMap<Integer, Object>() {{
                            put(1, Core.getCore().getUserManager().convertUniqueId(event.getConnection().getUniqueId()));
                        }}
                );

                xFunLogger.debug("Laddar användare till xFunProxy...");
                if (resultSet.next()) {
                    xFunLogger.debug("Användaren har en rank tilldelad till kontot - " + resultSet.getString("rank"));
                    rank = Rank.fromName(resultSet.getString("rank"));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            ProxyUser newUser = new ProxyUser(event.getConnection().getUniqueId(), rank);
            newUser.setFriends(friends);
        });
    }
}
