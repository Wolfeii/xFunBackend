package se.xfunserver.xfunbackend.proxy.assets.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
public class CommandProxyReload extends Command {

    public CommandProxyReload() {
        super("xfunproxyreload", "xfun.reload.modules", "xfunrl", "xfunreload", "proxyreload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length == 1) {
                for (ProxyModule proxyModule : Core.getCore().getProxyModules()) {
                    if (proxyModule.getShortname().toLowerCase().equalsIgnoreCase(args[0].toLowerCase())) {
                        if (proxyModule.isReloadable()) {
                            proxyModule.reload();
                            sender.sendMessage(new TextComponent(ChatColor.AQUA + "Modulen '" + proxyModule.getName() + "' har laddats om..."));
                            return;
                        } else {
                            sender.sendMessage(new TextComponent(ChatColor.RED + "Modulen '" + proxyModule.getName() + "' kan inte laddas om."));
                            return;
                        }
                    }
                }
            }
            sender.sendMessage(new TextComponent(ChatColor.RED + "Misslyckades att ladda om modulen " + args[0]));
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Användning: /xfunproxyreload <modul>"));
        }
    }
}
