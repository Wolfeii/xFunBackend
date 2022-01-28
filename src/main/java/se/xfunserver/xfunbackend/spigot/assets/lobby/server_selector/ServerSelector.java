package se.xfunserver.xfunbackend.spigot.assets.lobby.server_selector;

import lombok.Getter;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.assets.FileStructure;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSelector {

    @Getter
    private YMLFile selectorConfig;
    @Getter
    private Map<String, List<String>> itemData = new HashMap<>(),
            selectorData = new HashMap<>();

    public void setup() {
        Core.getPlugin().getPluginManager().getFileStructure()
                .add("server_selector", Core.getPlugin().getDataFolder().toString(), "server_selector", FileStructure.FileType.YML).save();

        try {
            this.selectorConfig = Core.getPlugin().getPluginManager().getFileStructure().getYMLFile("server_selector");

            this.selectorConfig.set(new HashMap<String, Object>() {{
                put("selector.name", "&b&lServer Namn");
                put("selector.position", "&b&lServer Namn");
                put("selector.gui-name", "&b&lServer Namn");

                put("gui-items.BARRIER.name", "&b&lServer Namn");
                put("gui-items.BARRIER.position", "&b&lServer Namn");
                put("gui-items.BARRIER.server", "&b&lServer Namn");
                put("gui-items.BARRIER.lor", "&b&lServer Namn");
            }});
        } catch (FileNotFoundException e) {
            xFunLogger.error(e.getMessage());
        }
    }

    public void loadData() {
        try {
            this.selectorConfig = Core.getPlugin().getPluginManager().getFileStructure().getYMLFile("server_selector");
        } catch (FileNotFoundException e) {
            xFunLogger.error(e.getMessage());
        }
    }
}
