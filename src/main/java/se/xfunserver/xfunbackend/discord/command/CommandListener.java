package se.xfunserver.xfunbackend.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.core.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;

import java.awt.*;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        String[] args = content.split(" ");
        String commandInput = args[0];

        // Fixes
        content = content.replaceAll(args[0], "");
        args = ArrayUtils.remove(args, 0);

        for (DiscordCommand command : Discord.getCommandManager().getCommands()) {
            if (command.getAliases().contains(commandInput)) {
                assert member != null;
                if (member.hasPermission(command.getRank().getHighestPermission())) {
                    if (Discord.getCommandManager().checkSpam(member, commandInput)) {
                        channel.sendMessage(
                                "**Ta det lugnt och spamma INTE kommandon** " + member.getAsMention() + "!"
                        ).queue();
                        break;
                    }
                    command.execute(member, channel, message, content, args);
                    break;
                } else {
                    channel.sendMessageEmbeds(
                            new MessageBuilder("Inte tillåtelse!",
                                    "Du har inte den tillåtelse som krävs för detta kommando!")
                                    .overwriteColor(Color.RED)
                                    .build()
                    ).queue();
                }
            }
        }
    }
}
