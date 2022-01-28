package se.xfunserver.xfunbackend.discord.ticket.listener;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.assets.Codes;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;

import java.util.Objects;

public class MessageReactionAddListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) {
            return;
        }

        if (event.getGuild().getIdLong() == Long.parseLong(Codes.XFUN_SERVER_DISCORD_ID)) {
            // Öppna Ticket
            if (event.getTextChannel().getIdLong() == Long.parseLong(Codes.SUPPORT_CHANNEL_ID)) {
                event.getTextChannel().removeReactionById(event.getMessageId(),
                        event.getReactionEmote().getEmote(), Objects.requireNonNull(event.getUser())).queue();
                Discord.getTicketsManager().createTicket(event.getMember());
            } else {
                // Stäng Ticket
                Discord.getTicketsManager().getTickets().forEach(ticket -> {
                    if (event.getTextChannel() == ticket.getTextChannel()) {
                        if (event.getReaction().getReactionEmote().getIdLong() == Codes.getIDWithoutName(Codes.YES_ICON_ID)) {
                            if (ticket.isClosable()) {
                                ticket.close();
                            }
                        } else if (event.getReaction().getReactionEmote().getIdLong() == Codes.getIDWithoutName(Codes.NO_ICON_ID)) {
                            // Ticket Closing Cancelled
                            event.getTextChannel().editMessageEmbedsById(
                                    event.getMessageId(),
                                    new MessageBuilder("Stängning avbruten!", "Stängningen av denna ticket har avbrytits!").build()
                            ).queue(message -> {
                                message.clearReactions().queue();
                            });

                            ticket.setClosable(false);
                        }
                    }
                });
            }
        }
    }
}
