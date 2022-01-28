package se.xfunserver.xfunbackend.spigot.assets.lobby;

import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.lobby.server_selector.ServerSelector;

public class LobbyManager extends Module {

    private ServerSelector serverSelector;

    public LobbyManager(Core plugin) {
        super(plugin, "Lobby Manager", true);

        this.serverSelector = new ServerSelector();
        this.serverSelector.setup();
    }

    @Override
    public void reload(String response) {
        this.serverSelector.loadData();
    }
}
