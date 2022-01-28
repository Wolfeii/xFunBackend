package se.xfunserver.xfunbackend.spigot.version;

import org.bukkit.Bukkit;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;

public class VersionManager extends Module {

    public VersionManager(Core plugin) {
        super(plugin, "Version Manager", false);
    }

    @Override
    public void reload(String response) {

    }

    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public boolean checkVersion(double version) {
        String pack = Bukkit.getServer().getClass().getPackage().getName().replaceAll("_", ".");
        return pack.contains(Double.toString(version));
    }
}
