package se.xfunserver.xfunbackend.discord.assets;

public class Codes {

    // MAIN DISCORD
    public static String XFUN_SERVER_DISCORD_ID = "255071054198734878";
    public static String SUPPORT_CHANNEL_ID =  "930319812259872788";
    public static String TICKET_CATEGORY_ID = "930203374320779274";
    public static String DISCORD_SUGGESTIONS_CHANNEL_ID = "930203513642962974";
    public static String CHANGELOG_ROLE_ID = "930203608736215081";
    public static String STAFF_ERROR_CHANNEL_ID = "";

    // ICONS
    public static String XFUN_SERVER_ICON_ID = "xfunserver:928631527460458596";
    public static String YES_ICON_ID = "ja:930204112732168232";
    public static String NO_ICON_ID = "nej:930204112732168232";

    public static Long getIDWithoutName(String id) {
        return Long.parseLong(id.split(":")[1]);
    }
}
