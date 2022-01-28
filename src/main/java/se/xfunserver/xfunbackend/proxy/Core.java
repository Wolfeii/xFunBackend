package se.xfunserver.xfunbackend.proxy;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.proxy.assets.AssetManager;
import se.xfunserver.xfunbackend.proxy.files.ProxyYMLFile;
import se.xfunserver.xfunbackend.proxy.friends.FriendManager;
import se.xfunserver.xfunbackend.proxy.plugin_messaging.PluginMessaging;
import se.xfunserver.xfunbackend.proxy.user.UserManager;
import se.xfunserver.xfunbackend.services.SQLService;
import se.xfunserver.xfunbackend.services.sql.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Core extends Plugin {

    @Getter
    public static Core core;
    private final List<ProxyModule> proxyModules = new ArrayList<>();

    private AssetManager assetManager;
    private PluginMessaging pluginMessaging;
    private DatabaseManager databaseManager;
    private FriendManager friendManager;
    private UserManager userManager;

    @Override
    public void onLoad() {
        core = this;
    }

    @Override
    public void onEnable() {
        Arrays.stream(xFunLogger.getLogos().logoNormal).forEach(s -> System.out.println(ChatColor.AQUA + s));
        System.out.println("  ");

        this.assetManager = new AssetManager(this);
        // Load all the settings e.t.c
        this.assetManager.initDiscordSettings();
        this.assetManager.loadDiscordSettings();

        ProxyYMLFile f = this.assetManager.getYmlFile();
        // Load the SQL Service so SQL can be used

        try {
            // Initialize SQL

            this.databaseManager = new DatabaseManager(new SQLService(
                    f.getConfiguration().getString("sql.host"),
                    f.getConfiguration().getString("sql.database"),
                    f.getConfiguration().getInt("sql.port"),
                    f.getConfiguration().getString("sql.username"),
                    f.getConfiguration().getString("sql.password")
            ));

            // Connect to SQL
            xFunLogger.info("Ansluter till SQL...");
            xFunLogger.info("Anslöt till din SQL Service Provider");
        } catch (Exception e) {
            xFunLogger.error("Jag kan inte ansluta till din SQL Service Provider");
            xFunLogger.error("Kan inte starta proxyn utan xFunBackend");
            getProxy().stop();
            return;
        }

        xFunLogger.setProduction(f.getConfiguration().getBoolean("server.isProduction"));

        // Load all the modules
        this.pluginMessaging = new PluginMessaging(this);

        if (xFunLogger.isProduction()) {
            Discord.initTerminal(f.getConfiguration().getString("discord.token"));
        } else {
            xFunLogger.warn("xFunBackend är i TEST läge! Produktion system är inte aktiva!");
        }

        this.assetManager.initServerChecker();

        xFunLogger.info("xFunBackend har laddats.");
    }

    @Override
    public void onDisable() {
        for (ProxyModule proxyModule : getProxyModules()) {
            proxyModule.deInit();
        }
    }
}
