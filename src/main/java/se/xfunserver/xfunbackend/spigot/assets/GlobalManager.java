package se.xfunserver.xfunbackend.spigot.assets;

import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.commands.CommandBackendReload;
import se.xfunserver.xfunbackend.spigot.listeners.EventHandlers;

public class GlobalManager extends Module {

    public GlobalManager(Core plugin) {
        super(plugin, "Global Manager", true);


        registerCommand(
                new CommandBackendReload(plugin)
        );

        registerListeners(
                new EventHandlers()
        );
    }

    @Override
    public void reload(String response) {

    }
}
