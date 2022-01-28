package se.xfunserver.xfunbackend.proxy.friends.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.friends.object.FriendRequest;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;
import se.xfunserver.xfunbackend.spigot.assets.C;

public class CommandFriend extends Command {

    public CommandFriend() {
        super("friends", "xfunbackend.friend.command", "friend", "f", "vän");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxyUser proxyUser = Core.getCore().getUserManager().getUser(((ProxiedPlayer) sender).getUniqueId());
            if (args.length > 0) {
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        // TODO: Ta bort vän från databas.

                        proxyUser.sendMessage("Tar bort vän...");
                        return;
                    }

                    ProxyUser targetUser = Core.getCore().getUserManager().getUser(args[1]);

                    if (targetUser == null || targetUser.getProxiedPlayer() == null) {
                        proxyUser.sendMessage(ChatColor.RED + args[1] + " är inte inne på servern, skrev du rätt?");
                        return;
                    }

                    if (args[0].equalsIgnoreCase("add")) {
                        FriendRequest friendRequest = new FriendRequest(proxyUser, targetUser);

                        if (!friendRequest.send()) {
                            proxyUser.sendMessage("Du har redan skickat en förfrågan till denna person.");
                            return;
                        }
                    } else {
                        FriendRequest friendRequest = Core.getCore().getFriendManager().getRequestByUser(proxyUser, targetUser);
                        if (friendRequest == null) {
                            proxyUser.sendMessage(ChatColor.RED + "Du har ingen förfrågan från " + args[1]);
                            return;
                        }

                        if (args[0].equalsIgnoreCase("accept")) {
                            friendRequest.onAccept();
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            friendRequest.onDeny();
                        }
                    }
                } else {
                    if (args[0].equalsIgnoreCase("list")) {
                        if (proxyUser.getFriends().size() > 0) {
                            for (int i = 0; i < proxyUser.getFriends().size(); i++) {
                                proxyUser.getProxiedPlayer().sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + (i + 1 + "") + ". " + proxyUser.getFriends().get(i)));
                            }
                        } else {
                            proxyUser.sendMessage(ChatColor.RED + "Du har inga vänner.");
                        }
                    }
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(" "));
                sender.sendMessage(TextComponent.fromLegacyText("/f add <player>"));
                sender.sendMessage(TextComponent.fromLegacyText("/f accept <player>"));
                sender.sendMessage(TextComponent.fromLegacyText("/f deny <player>"));
                sender.sendMessage(TextComponent.fromLegacyText("/f remove <player>"));
                sender.sendMessage(TextComponent.fromLegacyText("/f list"));
                sender.sendMessage(TextComponent.fromLegacyText(" "));
            }
        }
    }
}
