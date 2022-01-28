package se.xfunserver.xfunbackend.spigot.assets;

import lombok.Getter;
import org.bukkit.Bukkit;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.services.SQLService;
import se.xfunserver.xfunbackend.services.sql.DatabaseManager;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.api.xFunPlugin;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginManager extends Module {

    private Core plugin;

    @Getter
    private FileStructure fileStructure;

    @Getter
    private List<xFunPlugin> plugins = new ArrayList<>();

    public PluginManager(Core plugin) {
        super(plugin, "Plugin Manager", false);
        this.plugin = plugin;
    }

    @Override
    public void reload(String response) {

    }

    public boolean initDatabase() {
        try {
            // Initialize SQL
            YMLFile f = getPlugin().getPluginManager().getFileStructure().getYMLFile("settings");
            getPlugin().setDatabaseManager(new DatabaseManager(new SQLService(
                    f.get().getString("sql.host"),
                    f.get().getString("sql.database"),
                    f.get().getInt("sql.port"),
                    f.get().getString("sql.username"),
                    f.get().getString("sql.password")
            )));

            // Connect to SQL
            xFunLogger.info("Ansluter till SQL...");

            try {
                xFunLogger.success("Anslöt till din SQL Service Provider");
                getPlugin().getDatabaseManager().loadDefaultTables(); // Generera alla default tables ifall dom inte redan existerade
                return true;
            } catch (Exception e) {
                xFunLogger.error("Ett fel uppstod med att ansluta till MySQL. Något gick fel.", "Använder JSON som din primära databas!");
                Bukkit.getServer().shutdown();
                return false;
            }
        } catch (FileNotFoundException e) {
            xFunLogger.error("Filen 'settings.yml' existerar inte. Skapar en nu...", "Startar om plugin...");
            Bukkit.getServer().getPluginManager().enablePlugin(getPlugin());
            return false;
        } catch (Exception e) {
            xFunLogger.error("Jag kan inte ansluta till din SQL Service provider", e.getMessage(),
                    "Titta dina SQL inställningar i 'settings.yml' filen.", "Stänger av...");
            Bukkit.getServer().shutdown();
            return false;
        }
    }

    public void load() {
        this.fileStructure = new FileStructure();

        this.fileStructure
                .add("modules", null, "modules", FileStructure.FileType.YML)
                .add("settings", null, "settings", FileStructure.FileType.YML)
                .add("warps", null, "warps", FileStructure.FileType.JSON)
                .add("example_changelog", "changelogs/server", "example_changelog", FileStructure.FileType.YML)
                .add("sv", "lang", "sv_SE", FileStructure.FileType.YML)
                .add("offensive_words", null, "offensive", FileStructure.FileType.JSON)
                .add("status_settings", null, "status_settings", FileStructure.FileType.YML)
                .add("remote_database", null, "remote_database", FileStructure.FileType.YML)
                .save();

        try {
            this.fileStructure.getYMLFile("settings").set(
                    new HashMap<String, Object>() {{
                        put("sql.host", "127.0.0.1");
                        put("sql.database", "database");
                        put("sql.username", "username");
                        put("sql.password", "password");
                        put("sql.port", 3306);
                        put("network.name", "xFun Server");
                        put("network.isLobby", false);
                        put("network.isProduction", false);
                        put("network.serverName", "hub");
                    }}
            );

            this.fileStructure.getYMLFile("modules").setHeader("Stäng av dina moduler för att sänka tiden för pluginet att ladda. Kontakta Wolfeiii för en lista av moduler").set(
                    new HashMap<String, Object>() {{
                        put("modules.disabled", new ArrayList<String>());
                    }}
            );
        } catch (FileNotFoundException e) {
            Sam.getRobot().error(this, e.getMessage(), "Prova att kontakta server utvecklaren.", e);
        }
    }
}
