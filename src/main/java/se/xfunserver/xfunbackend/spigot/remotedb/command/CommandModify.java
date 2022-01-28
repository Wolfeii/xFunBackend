package se.xfunserver.xfunbackend.spigot.remotedb.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.command.xFunCommand;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import java.math.BigDecimal;

public class CommandModify extends xFunCommand {


    public CommandModify(Core plugin) {
        super(plugin, Rank.ADMIN,"Ett kommando som modifierar ekonomi databasen på xFun Main", true, "dbmodify");

        setPlayerUsage("(UPDATE/ADDITION/GET) <spelare> [mängd]");
        setConsoleUsage("(UPDATE/ADDITION) <spelare> <mängd>");
    }

    @Override
    public void execute(xFunUser user, Player player, String command, String[] args) {
        if (args.length > 1) {
            xFunUser targetUser = getPlugin().getUserManager().getFakeUser(args[1]);
            if (targetUser == null) {
                user.error(args[1] + " existerar inte i våran databas, eller är ej online.");
                return;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("GET")) {
                user.info("Spelaren " + args[1] + " har: " + getPlugin().getRemoteDatabaseManager().getCurrentCoins(targetUser.getUuid()));
            } else if (args.length == 3) {
                Long amount = null;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (Exception e) {
                    user.error(args[2] + " är inte en gitlig siffra (decimal)");
                    return;
                }

                if (args[0].equalsIgnoreCase("UPDATE")) {
                    getPlugin().getRemoteDatabaseManager().setCurrentCoins(targetUser.getUuid(), amount);
                    user.info("Satte " + targetUser.getName() + "'s antal MainMynt " +
                            "till " + amount);
                } else if (args[0].equalsIgnoreCase("ADDITION")) {
                    long currentAmount = getPlugin().getRemoteDatabaseManager().getCurrentCoins(targetUser.getUuid());

                    getPlugin().getRemoteDatabaseManager().setCurrentCoins(targetUser.getUuid(), currentAmount + amount);
                    user.info("Satte " + targetUser.getName() + "'s antal MainMynt " +
                            "till " + (currentAmount + amount));
                } else {
                    sendUsageMessage(player);
                }
            }
        } else {
            sendUsageMessage(player);
        }
    }

    @Override
    public void execute(ConsoleCommandSender sender, String command, String[] args) {
        if (args.length > 1) {
            xFunUser targetUser = getPlugin().getUserManager().getFakeUser(args[1]);
            if (targetUser == null) {
                sender.sendMessage(args[1] + " existerar inte i våran databas, eller är ej online.");
                return;
            }

            if (args.length == 3) {
                Long amount = null;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(args[2] + " är inte en gitlig siffra");
                    return;
                }

                if (args[0].equalsIgnoreCase("UPDATE")) {
                    getPlugin().getRemoteDatabaseManager().setCurrentCoins(targetUser.getUuid(), amount);
                    sender.sendMessage("Satte " + targetUser.getName() + "'s antal MainMynt " +
                            "till " + amount);
                } else if (args[0].equalsIgnoreCase("ADDITION")) {
                    long currentAmount = getPlugin().getRemoteDatabaseManager().getCurrentCoins(targetUser.getUuid());

                    getPlugin().getRemoteDatabaseManager().setCurrentCoins(targetUser.getUuid(), currentAmount + amount);
                    sender.sendMessage("Satte " + targetUser.getName() + "'s antal MainMynt " +
                            "till " + (currentAmount + amount));
                } else {
                    sendUsageMessage(sender);
                }
            }
        } else {
            sendUsageMessage(sender);
        }
    }
}
