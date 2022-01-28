package se.xfunserver.xfunbackend.spigot.botsam;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.botsam.object.ErrorHandler;
import se.xfunserver.xfunbackend.spigot.botsam.object.SamMessage;

import java.util.Arrays;

public class Sam {

    @Getter
    public final String prefix = "§3§lxFun Server §8</> " + ChatColor.GRAY;

    @Getter
    public final String announcement = "§3§lViktigt §8</> ";

    @Getter
    private static Sam robot;

    public Sam() {
        robot = this;
    }

    public static String getName() {
        return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "xFun Server SAM";
    }

    public void error(Module module, String cause, String solution, Exception exception) {
        new ErrorHandler(this).error(module, cause, solution, exception);
    }

    public void error(Module module, Exception exception) {
        new ErrorHandler(this).error(module, exception);
    }

    public void info(Player player, String... messages){
        Arrays.stream(messages).forEach(s -> player.sendMessage(prefix + C.CHAT.getColor() + C.translateColors(s)));
    }

    public void announcement(Player player, String... messages){
        Arrays.stream(messages).forEach(s -> player.sendMessage(announcement + C.CHAT.getColor() + C.translateColors(s)));
    }

    public void sam(Player player) {
        player.sendMessage(prefix + C.CHAT.getColor() + SamMessage.CANNOT_DO_THAT.getRandom());
    }

    public void warning(Player player, String... messages) {
        Arrays.stream(messages).forEach(s -> player.sendMessage(prefix + C.ERROR + (s == null ? SamMessage.ERROR.getRandom() : s)));
    }
}
