package se.xfunserver.xfunbackend.spigot.runnable;

import com.google.common.collect.Maps;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;

import java.util.Map;
import java.util.function.Consumer;

public class RunnableManager extends Module {

    private static final Map<String, xFunRunnable> RUNNABLES = Maps.newConcurrentMap();

    public RunnableManager(Core plugin) {
        super(plugin, "Runnable Manager", false);
    }

    @Override
    public void reload(String response) {

    }

    public xFunRunnable runTask(String name, Consumer<Core> run) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTask(getPlugin());
        return runnable;
    }

    public xFunRunnable runTaskAsynchronously(String name, Consumer<Core> run) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTaskAsynchronously(getPlugin());
        return runnable;
    }

    public xFunRunnable runTaskLater(String name, Consumer<Core> run, long time) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTaskLater(getPlugin(), time);
        return runnable;
    }

    public xFunRunnable runTaskLaterAsynchronously(String name, Consumer<Core> run, long time) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTaskLaterAsynchronously(getPlugin(), time);
        return runnable;
    }

    public xFunRunnable runTaskTimer(String name, Consumer<Core> run, long delay, long period) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTaskTimer(getPlugin(), delay, period);
        return runnable;
    }

    public xFunRunnable runTaskTimerAsynchronously(String name, Consumer<Core> run, long delay, long period) {
        xFunRunnable runnable = createRunnable(name, run);
        runnable.runTaskTimerAsynchronously(getPlugin(), delay, period);
        return runnable;
    }

    public void updateTime(String name, long delay, long period) {
        xFunRunnable runnable = RUNNABLES.get(name);

        if (runnable == null) {
            return;
        }

        xFunRunnable newRunnable = runnable.clone();

        RUNNABLES.remove(name);

        runTaskTimer(name, newRunnable.getRun(), delay, period);
    }

    private xFunRunnable createRunnable(String name, Consumer<Core> run) {
        xFunRunnable runnable = new xFunRunnable(getPlugin(), this, name, run);

        RUNNABLES.put(name, runnable);

        return runnable;
    }

    public Map<String, xFunRunnable> getRunnables() {
        return RUNNABLES;
    }

}