package se.xfunserver.xfunbackend.spigot.api;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.xfunserver.xfunbackend.assets.object.LinuxColorCodes;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;

public abstract class xFunPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!name().equalsIgnoreCase("xFunBackend")) {
            xFunLogger.info(xFunLogger.format("Extension", LinuxColorCodes.ANSI_CYAN, name() + " upptäckt..."));

            if (!Core.getPlugin().isLoaded()) {
                xFunLogger.error("xFunBackend hittades inte eller har ej startat! Stänger av extension...");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }

        try {
            preInit();
            init();

            if (!name().equalsIgnoreCase("xFunBackend")) {
                if (!Core.getPlugin().getPluginManager().getPlugins().contains(this)) {
                    Core.getPlugin().getPluginManager().getPlugins().add(this);
                }

                xFunLogger.info(
                        xFunLogger.format("Extension", LinuxColorCodes.ANSI_CYAN,
                                "Laddade totalt " + Module.getTotal() + " modules för extension '" + name() + "'!")
                );
                Module.total = 0;
            }
        } catch (Exception e) {
            xFunLogger.error("Fel uppstod med att ladda '" + name() + "'");
            xFunLogger.error("@ " + e.getStackTrace()[0].toString());
            xFunLogger.error(e.getMessage());
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        preDeInit();
        deinit();
    }

    public String name() { return "xFunBackend"; }
    public void preInit() {}
    public void preDeInit() {}

    public abstract void init();
    public abstract void deinit();
}
