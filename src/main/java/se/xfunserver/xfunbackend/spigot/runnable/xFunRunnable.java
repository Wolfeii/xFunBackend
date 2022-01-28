package se.xfunserver.xfunbackend.spigot.runnable;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import se.xfunserver.xfunbackend.spigot.Core;

import java.util.function.Consumer;

public class xFunRunnable extends BukkitRunnable implements Cloneable {

    private final Core plugin;
    private final RunnableManager runnableManager;

    @Getter
    private final String name;

    @Getter
    private final Consumer<Core> run;

    public xFunRunnable(Core plugin, RunnableManager runnableManager, String name, Consumer<Core> run) {
        this.plugin = plugin;
        this.runnableManager = runnableManager;
        this.name = name;
        this.run = run;
    }

    @Override
    public void run() {
        run.accept(plugin);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        runnableManager.getRunnables().remove(name);
    }

    @Override
    public xFunRunnable clone() {
        try {
            return (xFunRunnable) super.clone();
        } catch (Exception ex) {
            return null;
        }
    }
}
