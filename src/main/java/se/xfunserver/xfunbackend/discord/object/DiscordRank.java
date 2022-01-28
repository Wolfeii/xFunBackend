package se.xfunserver.xfunbackend.discord.object;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;

public enum DiscordRank {

    NONE(Permission.MESSAGE_SEND),
    STAFF(Permission.KICK_MEMBERS),
    ADMIN(Permission.ADMINISTRATOR);

    @Getter
    private final Permission highestPermission;

    DiscordRank(Permission permission){
        this.highestPermission = permission;
    }
}
