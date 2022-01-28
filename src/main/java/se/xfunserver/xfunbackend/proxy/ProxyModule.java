package se.xfunserver.xfunbackend.proxy;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

@Getter
public abstract class ProxyModule extends Plugin {

    @Getter
    private final Core plugin;
    private final String name;
    private final boolean reloadable;

    public ProxyModule(Core plugin, String name, boolean reloadable) {
        this.plugin = plugin;
        this.name = name;
        this.reloadable = reloadable;

        getPlugin().getProxyModules().add(this);
    }

    public abstract void reload();

    public abstract void deInit();

    public String getShortname() {
        return this.name.split(" ")[0];
    }

    public void registerCommands(Command... commands) {
        Arrays.asList(commands).forEach(
                command -> getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), command)
        );
    }

    public void registerListeners(Listener... listeners) {
        Arrays.asList(listeners).forEach(
                listener -> getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), listener)
        );
    }
}
