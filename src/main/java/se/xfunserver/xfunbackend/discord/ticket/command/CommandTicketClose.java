package se.xfunserver.xfunbackend.discord.ticket.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.assets.Codes;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;
import se.xfunserver.xfunbackend.discord.object.DiscordRank;
import se.xfunserver.xfunbackend.discord.ticket.object.Ticket;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;

import java.awt.*;

public class CommandTicketClose extends DiscordCommand {

    public CommandTicketClose() {
        super("Stäng en ticket (Endast i en Ticket!)", DiscordRank.NONE, "!", "close", "stäng");
    }

    @Override
    public void execute(Member member, MessageChannel channel, Message message, String rawContent, String[] args) {
        if (args.length == 0) {
            Ticket ticket = Discord.getTicketsManager().getTicketFromMessageChannel(channel);

            if (ticket == null || ticket.isClosable()) {
                return;
            }

            ticket.setClosable(true);

            channel.sendMessageEmbeds(
                    new MessageBuilder("Är du säker?", "Är du säker på att du vill stänga denna ticket?")
                            .overwriteColor(Color.ORANGE)
                            .build()
            ).queue(message1 -> {
                message1.addReaction(Codes.YES_ICON_ID).queue();
                message1.addReaction(Codes.NO_ICON_ID).queue();
            });
        }
    }
}
