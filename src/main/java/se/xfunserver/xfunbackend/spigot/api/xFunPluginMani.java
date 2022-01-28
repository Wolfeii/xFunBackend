package se.xfunserver.xfunbackend.spigot.api;

import org.bukkit.ChatColor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface xFunPluginMani {

    /**
     * The name of this backend system
     */
    String pluginName() default "xFunBackend";

    /**
     * The prefix of all our permissions
     * @IMPORTANT: Don't forget the . at the end!
     */
    String[] permissionPrefix() default "xfunbackend.";

    /**
     * The main color of this plugin
     */
    ChatColor pluginColor() default ChatColor.AQUA;
}
