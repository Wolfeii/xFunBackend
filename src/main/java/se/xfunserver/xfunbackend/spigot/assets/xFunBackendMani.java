package se.xfunserver.xfunbackend.spigot.assets;

import org.bukkit.ChatColor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface xFunBackendMani {

    String backendName() default "xFunBackend";

    String permissionPrefix() default "xfunbackend";

    ChatColor mainColor() default ChatColor.AQUA;

    String proxy() default "BungeeCord";

    String serverName() default "xFun";
}
