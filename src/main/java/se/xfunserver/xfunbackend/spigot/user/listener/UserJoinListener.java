package se.xfunserver.xfunbackend.spigot.user.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.user.UserManager;
import se.xfunserver.xfunbackend.spigot.user.event.UserLoadEvent;
import se.xfunserver.xfunbackend.spigot.user.event.UserPreLoadEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class UserJoinListener implements Listener {

    @EventHandler
    public void preUserJoin(AsyncPlayerPreLoginEvent event) {
        Core.getPlugin().getRunnableManager().runTaskAsynchronously(
                "Ladda User",
                core -> {
                    UserManager userManager = Core.getPlugin().getUserManager();
                    xFunUser user = userManager.getUser(event.getUniqueId());

                    if (user == null) {
                        xFunLogger.debug("Användaren som laddades är null.");
                        user = userManager.retrievePlayer(userManager.loadFromUUID(event.getUniqueId()), true);

                        if (user == null) {
                            xFunLogger.debug("Försöke ladda användare, fortfarande null...");
                            user = new xFunUser(
                                    event.getUniqueId(),
                                    event.getName(),
                                    Rank.NONE,
                                    UserLoadEvent.UserLoadType.NEW
                            );

                            xFunUser finalUser1 = user;
                            Core.getPlugin().getRunnableManager().runTask("hack main thread", core1 -> {
                                UserPreLoadEvent userPreLoadEvent = new UserPreLoadEvent(finalUser1);
                                Core.getPlugin().getServer().getPluginManager().callEvent(userPreLoadEvent);
                                finalUser1.attemptLoading();
                            });
                        } else {
                            xFunUser finalUser = user;
                            Core.getPlugin().getRunnableManager().runTask("hack main thread", core1 -> {
                                UserPreLoadEvent userPreLoadEvent = new UserPreLoadEvent(finalUser);
                                Core.getPlugin().getServer().getPluginManager().callEvent(userPreLoadEvent);
                                finalUser.attemptLoading();
                            });
                        }
                    }
                }
        );
    }

    @EventHandler
    public void onUserPreLogin(PlayerLoginEvent event) {
        xFunLogger.debug("Tittar ifall om servern är redo, resultat: " + Core.getPlugin().isReady());
        if (!Core.getPlugin().isReady()) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL,
                    "Servern laddar fortfarande..."
            );

            event.setKickMessage("\nServern laddar fortfarande...\nTillåt alla system att ladda upp först ;)");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        xFunUser user = Core.getPlugin().getUserManager().getUser(event.getPlayer().getUniqueId());
        if (user != null) {
            user.attemptLoading();
        }
    }
}
