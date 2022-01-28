package se.xfunserver.xfunbackend.spigot;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.services.sql.DatabaseManager;
import se.xfunserver.xfunbackend.spigot.assets.*;
import se.xfunserver.xfunbackend.spigot.assets.lobby.LobbyManager;
import se.xfunserver.xfunbackend.spigot.assets.object.Message;
import se.xfunserver.xfunbackend.spigot.api.xFunPlugin;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.botsam.object.ErrorHandler;
import se.xfunserver.xfunbackend.spigot.changelog.ChangelogManager;
import se.xfunserver.xfunbackend.spigot.command.CommandManager;
import se.xfunserver.xfunbackend.spigot.gui.GuiManager;
import se.xfunserver.xfunbackend.spigot.remotedb.RemoteDatabaseManager;
import se.xfunserver.xfunbackend.spigot.runnable.RunnableManager;
import se.xfunserver.xfunbackend.spigot.user.UserManager;
import se.xfunserver.xfunbackend.spigot.version.VersionManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@xFunBackendMani(backendName = "xFunBackend", mainColor = ChatColor.AQUA, permissionPrefix = "xfunserver", proxy = "BungeeCord", serverName = "xFun")
public final class Core extends xFunPlugin {

    @Getter @Setter
    private static Core plugin;

    private boolean loaded = false,
                    ready = false,
                    disabled = false;

    public List<Module> modules = new ArrayList<>();

    private PluginManager pluginManager;
    private VersionManager versionManager;
    private CommandManager commandManager;
    private RunnableManager runnableManager;
    @Setter
    private DatabaseManager databaseManager;
    private ChangelogManager changelogManager;
    private GlobalManager globalManager;
    private GuiManager GUIManager;
    private UserManager userManager;
    private StatusManager statusManager;
    private RemoteDatabaseManager remoteDatabaseManager;
    private Message message;
    private LobbyManager lobbyManager = null;
    private xFunBackendMani manifest;


    @Override
    public void init() {
    }

    @Override
    public void preInit() {
        setPlugin(this);

        xFunBinder binder = new xFunBinder(this);
        Injector injector = binder.createInjector();
        injector.injectMembers(this);

        this.versionManager = new VersionManager(this);

        Arrays.stream(xFunLogger.getLogos().logoNormal).forEach(s -> getServer().getConsoleSender().sendMessage(C.PLUGIN + s));
        System.out.println(" ");

        // Init SAM
        new Sam();
        getLogger().addHandler(new ErrorHandler(Sam.getRobot()));

        this.runnableManager = new RunnableManager(this);

        this.pluginManager = new PluginManager(this);
        this.pluginManager.load();

        // Load the Manifest so other it can be used in other classes
        this.manifest = this.getClass().getAnnotation(xFunBackendMani.class);

        // Load the SQL Service so SQL can be used
        if (!this.pluginManager.initDatabase()) {
            this.setEnabled(false);
            return;
        }

        try {
            xFunLogger.setProduction(getPluginManager().getFileStructure().getYMLFile("settings")
                    .get().getBoolean("network.isProduction"));
        } catch (FileNotFoundException e) {
            xFunLogger.error("Ett fel uppstod med att ladda settings.yml: " + e.getMessage());
        }

        this.message = new Message(this);

        // Initialize all the messages being sent
        try {
            this.message.init(getPluginManager().getFileStructure().getYMLFile("sv"));
            C.initColors();
        } catch (Exception e) {
            Sam.getRobot().error(null, e.getMessage(), "Rekommenderas att kontakta server utvecklaren Wolfeiii", e);
        }

        // Load the modules \\
        // WARNING !!! WARNING (Do not place any modules over this line)
        this.commandManager = new CommandManager(this);
        /////////////////////
        this.userManager = new UserManager(this);

        this.GUIManager = new GuiManager(this);
        this.statusManager = new StatusManager(this);
        this.changelogManager = new ChangelogManager(this);
        this.globalManager = new GlobalManager(this);
        this.remoteDatabaseManager = new RemoteDatabaseManager(this);

        try {
            if (getPluginManager().getFileStructure().getYMLFile("settings")
                    .get().getBoolean("network.isLobby")) {
                this.lobbyManager = new LobbyManager(this);
            }
        } catch (FileNotFoundException e) {
            xFunLogger.error(e.getMessage());
        }

        // Init Modules
        this.modules.forEach(Module::onInit);

        xFunLogger.info("Använder Version adapter: " + this.versionManager.getServerVersion());

        // Disable this to disable the API
        xFunLogger.setSpigotAPI(this);

        // Update all the messages being sent
        Message.update();

        xFunLogger.info("Laddade en total av " + Module.getTotal() + " moduler & system!");
        Module.total = 0;

        this.loaded = true;
        this.disabled = false;

        if (!xFunLogger.isProduction()) {
            xFunLogger.warn("xFunBackend kör i TEST läge! System som kräver produktions läge körs inte!");
        }

        xFunLogger.success("xFunBackend startades och kör korrekt.");

        // Calling the method once to main thread finished
        new BukkitRunnable() {
            @Override
            public void run() {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ready = true;
                    }
                }.runTaskLater(Core.this, 20L * 5);

                onServerEnabled();
            }
        }.runTask(this);
    }

    @Override
    public void deinit() {
        this.modules.forEach(Module::onDeInit);
    }

    private void onServerEnabled() {
        // Init Modules
        this.modules.forEach(Module::onServerLoad);
    }

    public void onRestart() {
        this.loaded = false;
        onDisable();
        onEnable();
    }
}
