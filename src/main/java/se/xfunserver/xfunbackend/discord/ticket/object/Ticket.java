package se.xfunserver.xfunbackend.discord.ticket.object;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;
import se.xfunserver.xfunbackend.spigot.utilities.UtilString;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Getter
public class Ticket {

    private final List<Member> members;
    private final String id;
    private TextChannel textChannel;

    @Setter
    private boolean closable = false;

    public Ticket(Member firstMember) {
        this.members = Lists.newArrayList();
        this.members.add(firstMember);
        this.id = firstMember.getEffectiveName().substring(0, 4) + UtilString.getAlphaNumericString(3);

        this.open();
    }

    public Ticket(String id, List<Member> members, TextChannel textChannel){
        this.id = id;
        this.members = Lists.newArrayList();
        this.members.addAll(members);
        this.textChannel = textChannel;
    }

    public boolean addMember(Member member){
        try{
            if (this.members.contains(member)){
                return false;
            }
            this.members.add(member);
            this.textChannel.createPermissionOverride(member).grant(EnumSet.of(
                    Permission.MESSAGE_SEND,
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_HISTORY
            )).queue();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void open() {
        this.createTicketChannel();
    }

    public void close() {
        this.textChannel.delete().queue();
        Discord.getTicketsManager().getTickets().remove(this);
    }

    private void createTicketChannel() {
        Objects.requireNonNull(Discord.getJda().getCategoryById(Discord.getTicketsManager().getTicketCategory()))
                .createTextChannel("ticket-" + this.id)
                .addPermissionOverride(
                        this.members.get(0),
                        EnumSet.of(
                                Permission.MESSAGE_SEND,
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_HISTORY
                        ),
                        null
                ).queue(textChannel -> {
                    this.textChannel = textChannel;

                    textChannel.sendMessageEmbeds(
                            new MessageBuilder(
                                    "Hejsan! F??lj g??rna dessa instruktioner!",
                                    "F??r att kunna assistera dig s?? bra som m??jligt, fr??gar vi dig om f??ljande information: \n\n"
                                            + "* Ditt Minecraft Anv??ndarnamn\n"
                                            + "* Din anledning f??r denna ticket\n\n"
                                            + "Tack, en medlem i v??rat staff team kommer hj??lpa dig s?? snart som m??jligt!\n"
                                            + "Anv??nd `!close` n??r som helst om du vill st??nga denna ticket!"
                            ).build()
                    ).queue();
                });
    }
}
