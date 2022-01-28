package se.xfunserver.xfunbackend.discord.command.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;
import se.xfunserver.xfunbackend.discord.object.DiscordRank;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;

public class CommandHelp extends DiscordCommand {

    public CommandHelp() {
        super("Visar en lista med alla tillängliga kommandon!", DiscordRank.NONE, "!", "help");
    }

    @Override
    public void execute(Member member, MessageChannel channel, Message message, String rawContent, String[] args) {
        MessageBuilder messageBuilder = new MessageBuilder(
                "En lista med alla tillängliga kommandon!",
                MessageBuilder.SetType.TITLE
        );

        Discord.getCommandManager().getCommands().forEach(discordCommand -> {
            if (discordCommand.isShowInHelp()) {
                messageBuilder.addField(
                        discordCommand.getAliases().get(0) +
                                (discordCommand.getUsage() == null ? "" : " " + discordCommand.getUsage()),
                        discordCommand.getDescription(),
                        false
                );
            }
        });

        channel.sendMessageEmbeds(
                messageBuilder.build()
        ).queue();
    }
}
