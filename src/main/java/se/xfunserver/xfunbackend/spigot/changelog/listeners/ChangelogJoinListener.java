package se.xfunserver.xfunbackend.spigot.changelog.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.changelog.object.Changelog;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;
import se.xfunserver.xfunbackend.spigot.utilities.UtilString;
import se.xfunserver.xfunbackend.spigot.utilities.UtilTime;

import java.util.Date;

public class ChangelogJoinListener implements Listener {

    @EventHandler
    public void onChangelogJoin(PlayerJoinEvent event) {
        Changelog changelog = Core.getPlugin().getChangelogManager().getLatestChangelog();
        if (changelog != null && UtilTime.daysBetween(changelog.getDate(), new Date()) <= 2) {
            xFunUser user = Core.getPlugin().getUserManager().getUser(event.getPlayer());

            user.message(
                    UtilString.centered("&3&lUPPDATERINGAR"),
                    " ",
                    "&7Har du tittat in dom senaste uppdateringarna?",
                    "&7Senaste uppdateringen var &b" + changelog.getNiceDate(),
                    "&7Titta in dessa changelogs med &b/changelog",
                    " "
            );
        }
    }
}
