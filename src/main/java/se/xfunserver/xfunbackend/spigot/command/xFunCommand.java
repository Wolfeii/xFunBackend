package se.xfunserver.xfunbackend.spigot.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.assets.object.Message;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.botsam.object.SamMessage;
import se.xfunserver.xfunbackend.spigot.command.event.xFunUserExecuteCommandEvent;
import se.xfunserver.xfunbackend.spigot.user.object.MessageModification;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import java.util.Arrays;

public abstract class xFunCommand extends Command implements PluginIdentifiableCommand {

    @Getter()
    private final Core plugin;

    @Getter
    private final Rank rank;
    private final boolean consoleAllowed;
    private final String description;
    private final String permission;
    private final String[] aliases;
    private String[] playerUsage, consoleUsage;

    @Setter
    private double cooldown = 1.0;

    public xFunCommand(Core plugin, String description, boolean consoleAllowed, String... aliases) {
        this(plugin,null, Rank.NONE, description, consoleAllowed, aliases);
    }

    public xFunCommand(Core plugin, String permission, String description, boolean consoleAllowed, String... aliases) {
        this(plugin, permission, Rank.NONE, description, consoleAllowed, aliases);
    }

    public xFunCommand(Core plugin, Rank rank, String description, boolean consoleAllowed, String... aliases) {
        this(plugin, null, rank, description, consoleAllowed, aliases);
    }


    protected xFunCommand(Core plugin, String permission, Rank rank, String description, boolean consoleAllowed, String... aliases) {
        super(aliases[0], description, aliases[0], Arrays.asList(aliases));

        this.plugin = plugin;
        this.rank = rank;
        this.consoleAllowed = consoleAllowed;
        this.description = description;
        this.permission = permission;
        this.playerUsage = aliases;
        this.consoleUsage = aliases;

        String[] as = new String[aliases.length];
        for (int index = 0; index < aliases.length; index++) {
            as[index] = aliases[index].toLowerCase();
        }

        this.aliases = as;
    }

    public void setPlayerUsage(String... usage) {
        this.playerUsage = usage;
    }

    public void setConsoleUsage(String... usage) {
        this.consoleUsage = usage;
    }

    public boolean hasPermission() {
        return this.permission != null;
    }

    public void couldNotFind(Player p, String thing, String attempt) {
        p.sendMessage(
                Message.format("command.player.not_found", null)
        );
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            xFunUser user = getPlugin().getUserManager().getUser((Player) sender);

            if (this.hasPermission()) {
                if (!user.hasPermission(this.permission)) {
                    return false;
                }
            }

            if (this.rank.getId() > user.getRank().getId()) {
                user.error(SamMessage.NO_PERMISSIONS.getRandom());
                return false;
            }

            try {
                xFunUserExecuteCommandEvent executeCommandEvent =
                        new xFunUserExecuteCommandEvent(this, user);
                getPlugin().getServer().getPluginManager().callEvent(executeCommandEvent);
                if (executeCommandEvent.isCancelled()) {
                    return true;
                }

                this.execute(user, (Player) sender, commandLabel, args);
            } catch (Exception e) {
                user.error("Något gick fel med att använda detta kommando.");
                Sam.getRobot().error(Core.getPlugin().getCommandManager(), e);
                user.sendModificationMessage(
                        MessageModification.RAW,
                        ChatColor.RED + "En automatisk rapport har skickats in!"
                );
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (this.consoleAllowed) {
                this.execute((ConsoleCommandSender) sender, commandLabel, args);
            }
        }

        return true;
    }

    public abstract void execute(xFunUser user, Player player, String command, String[] args);
    public abstract void execute(ConsoleCommandSender sender, String command, String[] args);

    public void sendUsageMessage(Player player) {
        StringBuilder usageString = new StringBuilder();

        if(playerUsage.length != 0) {
            for(String s : playerUsage)
                usageString.append(s).append(" ");
        }

        player.sendMessage(C.ERROR.getColor() + "Användning: /" + aliases[0] + (playerUsage.length != 0 ? " " + usageString
                .toString().trim() : ""));
    }

    public void sendUsageMessage(ConsoleCommandSender console) {
        String usageString = "";

        if(consoleUsage.length != 0) {
            for(String s : consoleUsage)
                usageString+=s + " ";
        }

        console.sendMessage("Användning: /" + aliases[0] + (consoleUsage.length != 0 ? " " + usageString.trim() : ""));
    }
}