package se.xfunserver.xfunbackend.spigot.command;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.SimplePluginManager;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.object.Message;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;

import java.lang.reflect.Field;
import java.util.*;

public class CommandManager extends Module {

    private final List<xFunCommand> commands = Lists.newArrayList();
    public CommandManager(Core plugin) {
        super(plugin, "Command Manager", false);

        Message.add(new HashMap<String, Object>() {{
            put("player.not_found", "Hittade inte spelaren %player%.");
        }});
    }

    @Override
    public void reload(String response) {

    }

    private static CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    private Map<String, Command> getKnownCommands(SimpleCommandMap commandMap) {
        Map<String, Command> knownCommands = null;
        try {
            Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            knownCommands  = (Map<String, Command>) f.get(commandMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return knownCommands;
    }

    public boolean isCommand(String command){
        for(xFunCommand cmd : commands){
            for(String alias : cmd.getAliases()){
                if (alias.equalsIgnoreCase(command)){
                    return true;
                }
            }
        }
        return false;
    }

    public void registerCommand(xFunCommand command){
        this.commands.add(command);
        try {
            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            simpleCommandMap.register(command.getName(), "xfun", command);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    public void unregisterMinecraftCommand(String name) {
        PluginCommand command = getPlugin().getServer().getPluginCommand(name);

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            command.unregister(getCommandMap());
            getKnownCommands((SimpleCommandMap) getCommandMap()).remove(name);
        }, 1L);
    }


    public List<xFunCommand> getCommands() {
        return commands;
    }
}
