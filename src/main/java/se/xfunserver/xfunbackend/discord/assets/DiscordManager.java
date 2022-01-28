package se.xfunserver.xfunbackend.discord.assets;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.discord.DiscordModule;
import se.xfunserver.xfunbackend.discord.command.command.CommandChangelog;
import se.xfunserver.xfunbackend.discord.command.command.CommandHelp;
import se.xfunserver.xfunbackend.discord.utilities.MessageBuilder;

import java.awt.*;
import java.util.Date;
import java.util.Objects;

public class DiscordManager extends DiscordModule {

    private String exceptionMessageCache = "";

    public DiscordManager() {
        super("Discord Manager");

        registerCommandListeners(
                new CommandHelp(),
                new CommandChangelog()
        );
    }

    @Override
    public void init(JDA jda) {

    }

    public void automatedError(String exceptionMessage, String exception, String stackTrace) {
        if (this.exceptionMessageCache.equals(exceptionMessage)) {
            return;
        }

        this.exceptionMessageCache = exceptionMessage;
        Objects.requireNonNull(Discord.getJda()
                .getTextChannelById(Codes.STAFF_ERROR_CHANNEL_ID))
                .sendMessageEmbeds(
                        new MessageBuilder("Exception Fel | Automatiskt System", MessageBuilder.SetType.TITLE)
                                .addField("Exception", exception, false)
                                .addField("Exception Meddelande", exceptionMessage, false)
                                .addField("Stacktrace", stackTrace, false)
                                .setFooter(new Date().toString())
                                .overwriteColor(Color.RED)
                                .build()
                ).queue();
    }
}
