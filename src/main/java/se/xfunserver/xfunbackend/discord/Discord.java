package se.xfunserver.xfunbackend.discord;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.discord.assets.DiscordManager;
import se.xfunserver.xfunbackend.discord.command.CommandManager;
import se.xfunserver.xfunbackend.discord.ticket.TicketsManager;

@Getter
public class Discord {

    @Getter
    public static JDA jda;

    @Getter
    public static CommandManager commandManager;
    @Getter
    public static DiscordManager discordManager;
    @Getter
    public static TicketsManager ticketsManager;

    public static void initTerminal(String token) {
        xFunLogger.discord("Laddar JDA API...");

        try {
            xFunLogger.discord("Laddar Discord Botten...");

            JDABuilder builder = JDABuilder.createDefault(token)
                    .setAutoReconnect(true)
                    .setActivity(Activity.playing("mc.xfunserver.se"))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .enableIntents(GatewayIntent.GUILD_EMOJIS)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            xFunLogger.discord("Discord Bot laddad, laddar bot moduler...");

            commandManager = new CommandManager();
            discordManager = new DiscordManager();
            ticketsManager = new TicketsManager();

            xFunLogger.discord("Alla bot moduler har laddats.");

            DiscordModule.getModules().forEach(discordModule -> {
                if (discordModule.getListeners() != null) {
                    builder.addEventListeners(discordModule.getListeners());
                }
            });

            jda = builder.build().awaitReady();

            DiscordModule.getModules().forEach(discordModule -> {
                discordModule.init(jda);
            });

            xFunLogger.discord("Discord bot har startats och är nu aktiv.");
        } catch (Exception e) {
            xFunLogger.error("Discord misslyckades att ladda!");
            xFunLogger.error(e.getMessage());
        }
    }
}
