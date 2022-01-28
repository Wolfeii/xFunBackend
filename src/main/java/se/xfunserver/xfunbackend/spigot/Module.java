package se.xfunserver.xfunbackend.spigot;

import lombok.Getter;
import org.bukkit.event.Listener;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.command.xFunCommand;

import java.util.Arrays;

@Getter
public abstract class Module implements Listener {

    private final Core plugin;
    private final String name;
    private final Sam sam;

    private final boolean reloadable;
    private boolean disabled = false;

    @Getter
    public static int total = 0;

    public Module(Core plugin, String name, boolean reloadable) {
        this.plugin = plugin;
        this.name = name;
        this.sam = new Sam();
        this.reloadable = reloadable;
        total++;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        getPlugin().getModules().add(this);
    }

    public void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener ->
                getPlugin().getServer().getPluginManager().registerEvents(listener, plugin));
    }

    public void registerCommand(xFunCommand... commands) {
        for (xFunCommand command : commands) {
            plugin.getCommandManager().registerCommand(command);
        }
    }

    public String getShortname() {
        return getName().split(" ")[0];
    }

    public abstract void reload(String response);

    public void onServerLoad() {}
    public void onInit() {}
    public void onDeInit() {}

    public void disable() {
        this.disabled = true;
    }
}
