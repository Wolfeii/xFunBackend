package se.xfunserver.xfunbackend.proxy.assets;

import lombok.Getter;
import net.md_5.bungee.api.config.ServerInfo;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
import se.xfunserver.xfunbackend.proxy.assets.commands.CommandProxyReload;
import se.xfunserver.xfunbackend.proxy.assets.listeners.AutomatedReportsListener;
import se.xfunserver.xfunbackend.proxy.files.ProxyYMLFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class AssetManager extends ProxyModule {

    private ProxyYMLFile ymlFile;

    private final Map<String, String> discordChannels = new HashMap<>();
    private final List<String> offlineServers = new ArrayList<>();

    public AssetManager(Core plugin) {
        super(plugin, "Assets Manager", true);

        registerListeners(
                new AutomatedReportsListener()
        );

        registerCommands(
                new CommandProxyReload()
        );

        initServerChecker();
    }

    @Override
    public void reload() {
        loadDiscordSettings();
    }

    @Override
    public void deInit() {

    }

    public void loadDiscordSettings() {
        for (String server : getPlugin().getProxy().getServers().keySet()) {
            if (this.ymlFile.getConfiguration().contains("discord.channels." + server)) {
                discordChannels.put(server, this.ymlFile.getConfiguration().getString("discord.channels." + server));
            } else {
                xFunLogger.error("Ett fel uppstod med att ladda discord inställningar för server: " + server);
            }
        }
    }

    public void initDiscordSettings() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("discord.token", "SECRET_BOT_TOKEN_HERE");

        for (String server : getPlugin().getProxy().getServers().keySet()) {
            map.put("discord.channels." + server, "0000000000000000");
        }

        map.put("sql.host", "127.0.0.1");
        map.put("sql.database", "database");
        map.put("sql.username", "username");
        map.put("sql.password", "password");
        map.put("sql.port", 3306);

        map.put("network.isProduction", false);

        this.ymlFile = new ProxyYMLFile(getPlugin(), getPlugin().getDataFolder(), "settings");
        this.ymlFile.set(map);
    }

    public void initServerChecker() {
        getPlugin().getProxy().getScheduler().schedule(getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (ServerInfo server : getPlugin().getProxy().getServers().values()) {
                    server.ping((serverPing, throwable) -> {
                        if (throwable != null) {
                            if (!AssetManager.this.offlineServers.contains(server.getName())) {
                                AssetManager.this.offlineServers.add(server.getName());
                                xFunLogger.error(server.getName() + " kan inte pingas! Kanske är offline!");
                            }
                        } else {
                            if (AssetManager.this.offlineServers.contains(server.getName())) {
                                AssetManager.this.offlineServers.remove(server.getName());
                                xFunLogger.success(server.getName() + " är online igen.");
                            }
                        }
                    });
                }
            }
        }, 1, 2, TimeUnit.SECONDS);
    }
}
