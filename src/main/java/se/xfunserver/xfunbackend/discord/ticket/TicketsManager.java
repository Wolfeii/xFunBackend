package se.xfunserver.xfunbackend.discord.ticket;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import se.xfunserver.xfunbackend.discord.DiscordModule;
import se.xfunserver.xfunbackend.discord.assets.Codes;
import se.xfunserver.xfunbackend.discord.ticket.command.CommandTicketClose;
import se.xfunserver.xfunbackend.discord.ticket.listener.MessageReactionAddListener;
import se.xfunserver.xfunbackend.discord.ticket.object.Ticket;

import java.util.List;
import java.util.Objects;

public class TicketsManager extends DiscordModule {

    @Getter
    private final String ticketCategory = "930203374320779274";

    @Getter
    private final List<Ticket> tickets;

    public TicketsManager() {
        super("Tickets Manager");

        this.tickets = Lists.newArrayList();

        registerEventListeners(
                new MessageReactionAddListener()
        );

        registerCommandListeners(
                new CommandTicketClose()
        );
    }

    public void initialize(JDA jda) {
        Objects.requireNonNull(jda.getCategoryById(Codes.TICKET_CATEGORY_ID))
                .getTextChannels().forEach(textChannel -> {
                    String id = textChannel.getName().split("-")[1];
                    Ticket ticket = new Ticket(id, textChannel.getManager().getChannel().getMembers(), textChannel);
                    this.tickets.add(ticket);
                });
    }

    public int getTicketCountForMember(Member member) {
        return (int) this.tickets.stream().filter(ticket -> ticket.getMembers().contains(member)).count();
    }

    public Ticket getTicketFromMessageChannel(MessageChannel messageChannel) {
        for (Ticket ticket : this.tickets) {
            if (ticket.getTextChannel() == messageChannel) {
                return ticket;
            }
        }
        return null;
    }

    public void createTicket(Member member) {
        if (getTicketCountForMember(member) > 1) {
            return;
        }

        Ticket ticket = new Ticket(member);
        this.tickets.add(ticket);
    }

    @Override
    public void init(JDA jda) {
        Objects.requireNonNull(jda.getTextChannelById(Codes.SUPPORT_CHANNEL_ID))
                .addReactionById("930319845650755615", Codes.XFUN_SERVER_ICON_ID).queue();

        initialize(jda);
    }
}
