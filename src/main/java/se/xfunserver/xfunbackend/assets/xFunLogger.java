package se.xfunserver.xfunbackend.assets;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import se.xfunserver.xfunbackend.assets.letters.LetterGenerator;
import se.xfunserver.xfunbackend.assets.letters.Logo;
import se.xfunserver.xfunbackend.assets.object.LinuxColorCodes;
import se.xfunserver.xfunbackend.spigot.Core;

import java.util.Arrays;
import java.util.Random;

public class xFunLogger {

    public final static String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    @Getter @Setter
    private static boolean production = false;

    public enum xFunColor {
        CHAT(ChatColor.GRAY),
        INFO(ChatColor.AQUA),
        PRIMARY(ChatColor.AQUA),
        MESSAGE_HIGHLIGHT(ChatColor.AQUA),
        ERROR(ChatColor.RED),
        SUCCESS(ChatColor.GREEN),
        SAM(ChatColor.LIGHT_PURPLE),
        PLUGIN(ChatColor.GRAY);

        @Getter
        private final ChatColor color;

        xFunColor(ChatColor color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return getColor().toString();
        }

        public static String rainbow(String original) {
            StringBuilder builder = new StringBuilder();
            for (char c : original.toCharArray()) {
                ChatColor color = ChatColor.values()[new Random(ChatColor.values().length).nextInt()];
                builder.append(color).append(String.valueOf(c));
            }

            return builder.toString();
        }

        public static String getLine(){
            return "«§8§m----------------------------------§r»";
        }

        public static String getLineWithoutSymbols(){
            return "---------------------------------------";
        }

        public static String getLineWithName() {
            return "«§8§m-------------" + ChatColor.RESET + " §3§lxFun Server " + "§8§m-------------»";
        }

        public static String getLineWithName(String name) {
            return "«§8§m-------------" + ChatColor.RESET + " §b§l" + name + " " + "§8§m-------------»";
        }

        public static String getShortLineWithName(String name) {
            return "«§8§m--------" + ChatColor.RESET + " §b§l" + name + " " + "§8§m--------»";
        }

        public static String getLineWithNameWithoutSymbols(String name) {
            return "§8§m-------------" + ChatColor.RESET + " §b§l" + name + " " + "§8§m-------------";
        }

        public static String getShortLineWithNameWithoutSymbols(String name) {
            return "§8§m--------" + ChatColor.RESET + " §b§l" + name + " " + "§8§m--------";
        }
    }

    @Setter @Getter
    private static Core spigotAPI;

    @Getter
    private static final LetterGenerator letterGenerator = new LetterGenerator();

    @Getter
    private static final Logo logos = new Logo();

    public static void debug(String... messages) {
        if (isProduction()) return;
        Arrays.stream(messages).forEach(s -> System.out.println(LinuxColorCodes.ANSI_BLUE + "[xFun Debug] " + LinuxColorCodes.ANSI_CYAN + s + LinuxColorCodes.ANSI_RESET));
    }

    public static void info(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("Information", LinuxColorCodes.ANSI_YELLOW, s)));
    }

    public static void discord(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("Bot", LinuxColorCodes.ANSI_YELLOW, s)));
    }

    public static void success(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("Success", LinuxColorCodes.ANSI_GREEN, s)));
    }

    public static void normal(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("", LinuxColorCodes.ANSI_RESET, s)));
    }

    public static void error(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("Error", LinuxColorCodes.ANSI_RED, s)));
    }

    public static void warn(String... messages){
        Arrays.stream(messages).forEach(s -> System.out.println(format("Varning", LinuxColorCodes.ANSI_RED, s)));
    }


    public static String format(String prefix, String color, String message) {
        return LinuxColorCodes.ANSI_CYAN + "xFun" + (prefix.length() > 0 ? " " + prefix : "") + LinuxColorCodes.ANSI_BLACK + " </> " + color + message + LinuxColorCodes.ANSI_RESET;
    }

    // xFun Info </> Ett fel har uppstått.
}
