package se.xfunserver.xfunbackend.spigot.changelog.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.changelog.gui.ChangelogMainGUI;
import se.xfunserver.xfunbackend.spigot.command.xFunCommand;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class CommandChangelog extends xFunCommand {

    public CommandChangelog(Core plugin) {
        super(plugin, "Ett kommando för att se våran changelog", false, "changelog");
    }

    @Override
    public void execute(xFunUser user, Player player, String command, String[] args) {
        new ChangelogMainGUI(getPlugin()).open(player);
    }

    @Override
    public void execute(ConsoleCommandSender sender, String command, String[] args) {

    }
}
