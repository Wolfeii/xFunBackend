package se.xfunserver.xfunbackend.spigot.user.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.user.event.UserQuitEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class UserQuitListener implements Listener {

    @EventHandler
    public void onUserQuit(PlayerQuitEvent event) {
        xFunUser user = Core.getPlugin().getUserManager().getUser(event.getPlayer());

        event.setQuitMessage(null);
        if (user != null) {
            user.updateDatabase(true);

            Core.getPlugin().getServer().getPluginManager().callEvent(new UserQuitEvent(user, event.getPlayer()));
        }
    }
}
