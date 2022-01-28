package se.xfunserver.xfunbackend.spigot.assets;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.assets.object.Message;

import java.util.Arrays;

public enum C {

    CHAT(ChatColor.GRAY, "colors"),
    INFO(ChatColor.AQUA, "colors"),
    PRIMARY(ChatColor.GRAY, "colors"),
    MESSAGE_HIGHLIGHT(ChatColor.AQUA, "colors"),
    ERROR(ChatColor.RED, "colors"),
    SUCCESS(ChatColor.RED, "colors"),
    CHAT_HIGHLIGHT(ChatColor.AQUA, "colors"),
    PLUGIN(ChatColor.DARK_AQUA, "colors");

    public enum Symbol{
        HEARTH("❤"),
        KLAVER("♠"),
        CROSS("✘"),
        YES("✔");

        private final String symbol;

        Symbol(String symbol){
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return this.symbol;
        }
    }

    @Getter @Setter
    private ChatColor color;
    @Getter
    private String messageKey;

    C(ChatColor chatColor, String messageKey){
        this.color = chatColor;
        this.messageKey = messageKey;
    }

    public static void initColors() {
        for (C color : values()) {
            color.setColor(
                    ChatColor.getByChar(
                            Message.format(
                                    color.messageKey + "." + color.toString().toLowerCase(),
                                    "&" + color.getColor().getChar()
                            ).replace("&", "")
                    )
            );
        }
    }

    public static String translateColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getLine(){
        return "«§m----------------------------------§r»";
    }

    public static String getLineWithoutSymbols(){
        return "---------------------------------------";
    }

    public static String getLineWithName(){
        return "«" + ChatColor.STRIKETHROUGH + "-------------- " + Core.getPlugin().getManifest().backendName() + " -----------------" + ChatColor.RESET + "»";
    }

    public static String getLineWithName(String name){
        return "«" + ChatColor.STRIKETHROUGH + "--------------- " + ChatColor.AQUA + name + ChatColor.RESET + ChatColor.STRIKETHROUGH + " ----------------" + ChatColor.RESET + "»";
    }

    public static String getShortLineWithName(String name){
        return "«" + ChatColor.STRIKETHROUGH + "-------- " + ChatColor.AQUA + name + ChatColor.RESET + ChatColor.STRIKETHROUGH + " --------" + ChatColor.RESET + "»";
    }

    public static String getLineWithNameNoAttr(String name){
        return "«" + ChatColor.STRIKETHROUGH + "--------- " + C.INFO.getColor() + name + ChatColor.RESET + ChatColor.STRIKETHROUGH + " ---------" + ChatColor.RESET + "»";
    }

    public static String getLineWithNameWithoutSymbols(String name){
        return ChatColor.STRIKETHROUGH + "" + ChatColor.YELLOW + "-------------- " + ChatColor.AQUA + name + ChatColor.RESET + ChatColor.STRIKETHROUGH + " " + ChatColor.YELLOW + "---------------" + ChatColor.RESET;
    }

    public static String getShortLineWithNameWithoutSymbols(String name){
        return ChatColor.STRIKETHROUGH + "-------- " + ChatColor.AQUA + name + ChatColor.RESET + ChatColor.STRIKETHROUGH + " --------" + ChatColor.RESET;
    }

    public static void sendConsoleColors(String... messages) {
        Arrays.stream(messages).forEach(s -> Core.getPlugin().getServer().getConsoleSender().sendMessage(s));
    }

    public static String colorize(String value){
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    @Override
    public String toString() {
        return getColor()+"";
    }
}
