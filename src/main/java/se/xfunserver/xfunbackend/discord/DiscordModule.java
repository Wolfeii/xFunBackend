package se.xfunserver.xfunbackend.discord;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;
import se.xfunserver.xfunbackend.proxy.Core;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class DiscordModule {

    @Getter
    private static List<DiscordModule> modules = Lists.newArrayList();

    @Getter
    private final String name;
    @Getter
    private Object[] listeners;

    public DiscordModule(String name) {
        this.name = name;
        modules.add(this);
    }

    public abstract void init(JDA jda);

    public void registerEventListeners(Object... listeners) {
        this.listeners = listeners;
    }

    public void registerCommandListeners(DiscordCommand... discordCommands) {
        for (DiscordCommand command : discordCommands) {
            Discord.getCommandManager().addCommand(command);
        }
    }

    public void thread(Runnable runnable, long delay, long period, TimeUnit timeUnit){
        Core.getCore().getProxy().getScheduler().schedule(Core.getCore(), runnable, delay, period, timeUnit);
    }
}
