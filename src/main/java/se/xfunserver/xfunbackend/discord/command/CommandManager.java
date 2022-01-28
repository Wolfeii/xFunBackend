package se.xfunserver.xfunbackend.discord.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import se.xfunserver.xfunbackend.discord.DiscordModule;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandManager extends DiscordModule {

    @Getter
    private final List<DiscordCommand> commands;
    private final Map<Member, Integer> spamming;
    private final Map<Member, String> cache;

    private final int maxCommands = 2;

    public CommandManager() {
        super("Command Manager");

        this.spamming = Maps.newHashMap();
        this.cache = Maps.newHashMap();
        this.commands = Lists.newArrayList();

        registerEventListeners(
                new CommandListener()
        );

        thread(() -> {
            this.spamming.clear();
            this.cache.clear();
        }, 1, 15, TimeUnit.SECONDS);
    }

    @Override
    public void init(JDA jda) {

    }

    public boolean checkSpam(Member member, String command) {
        if (isSameMessage(member, command)) {
            if (this.spamming.containsKey(member)) {
                int spamming = this.spamming.get(member);
                if (spamming >= this.maxCommands) {
                    if (spamming >= this.maxCommands + 2) {
                        this.spamming.remove(member);
                    }
                    return true;
                }
                this.spamming.put(member, ++spamming);
            } else {
                this.spamming.put(member, 1);
            }
        }
        return false;
    }

    public boolean isSameMessage(Member member, String command) {
        if (this.cache.containsKey(member)) {
            return this.cache.get(member).contains(command);
        } else {
            this.cache.put(member, command);
        }
        return false;
    }

    public void addCommand(DiscordCommand discordCommand) {
        if (!this.commands.contains(discordCommand)) {
            this.commands.add(discordCommand);
        }
    }

}
