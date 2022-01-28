package se.xfunserver.xfunbackend.spigot.assets.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.command.xFunCommand;
import se.xfunserver.xfunbackend.spigot.user.object.MessageModification;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class CommandBackendReload extends xFunCommand {

    public CommandBackendReload(Core plugin) {
        super(plugin, "command.backendreload", "xFunBackend's officiella reload kommando", true, "backendreload", "backreload", "backrl", "backr");

        setPlayerUsage("<module>");
        setConsoleUsage("<module>");
    }

    @Override
    public void execute(xFunUser user, Player player, String command, String[] args) {
        if (args.length == 1) {
            for (Module module : Core.getPlugin().getModules()) {
                if (module.getShortname().toLowerCase().equalsIgnoreCase(args[0].toLowerCase())) {
                    if (module.isReloadable()) {
                        user.message("Laddar om " + module.getName() + "...");
                        module.reload("accept");
                        user.info("Laddade om modulen för dig.");
                        return;
                    } else {
                        user.warning("Denna modul låter mig inte ladda om den!");
                        return;
                    }
                }
            }
            user.warning("Jag kan inte hitta modulen " + args[0] + "...");
        } else {
            user.sendModificationMessage(
                    MessageModification.RAW,
                    C.getLineWithName("Reloadable Moduler")
            );
            for (Module module : Core.getPlugin().getModules()) {
                if (module.isReloadable()) {
                    user.sendModificationMessage(
                            MessageModification.CENTERED,
                            "&3" + module.getName() + " &8- &7/" + command + " " + module.getShortname()
                    );
                }
            }
        }
    }

    @Override
    public void execute(ConsoleCommandSender sender, String command, String[] args) {

    }
}
