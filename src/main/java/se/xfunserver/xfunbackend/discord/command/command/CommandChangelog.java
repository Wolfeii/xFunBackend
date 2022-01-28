package se.xfunserver.xfunbackend.discord.command.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import se.xfunserver.xfunbackend.discord.assets.Codes;
import se.xfunserver.xfunbackend.discord.command.object.DiscordCommand;
import se.xfunserver.xfunbackend.discord.object.DiscordRank;

import java.util.Objects;

public class CommandChangelog extends DiscordCommand {

    public CommandChangelog() {
        super("Lägger till dig i en notifikations lista när vi har gjort en ny ändring.",
                DiscordRank.NONE, "!", "changelog");
    }

    @Override
    public void execute(Member member, MessageChannel channel, Message message, String rawContent, String[] args) {
        member.getGuild().addRoleToMember(member,
                Objects.requireNonNull(member.getGuild().getRoleById(Codes.CHANGELOG_ROLE_ID)))
                .queue(aVoid -> {
                    channel.sendMessage("Lade till dig " + member.getAsMention()).queue();
                }, throwable -> {
                    // TODO: Failure
                });
    }
}
