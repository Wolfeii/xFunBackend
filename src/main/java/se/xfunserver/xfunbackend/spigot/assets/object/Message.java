package se.xfunserver.xfunbackend.spigot.assets.object;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import se.xfunserver.xfunbackend.assets.Pair;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Message extends Module {

    @Getter
    private static HashMap<String, Object> messageCache = new HashMap<>();
    @Getter
    private static YMLFile messageFile;

    public Message(Core plugin) {
        super(plugin, "Message Manager", false);
    }

    @Override
    public void reload(String response) {

    }

    public static String format(String key, String defaultMessage, Pair<String, String>... replaceList) {
        String message;
        if (getMessageCache().containsKey("messages." + key)) {
            message = getMessageCache().get("messages." + key).toString();
        } else {
            message = defaultMessage;
            getMessageCache().put("messages." + key, defaultMessage);
        }

        List<Pair<String, String>> replace = new ArrayList<>();

        if (replaceList.length > 0) {
            replace.addAll(Arrays.asList(replaceList));
        }

        for (C color : C.values()) {
            replace.add(new Pair<>(
                    "color_" + color.toString().toLowerCase(),
                    "&" + color.getColor().getChar()
            ));
        }

        if (replace.size() > 0) {
            for (Pair<String, String> map : replace) {
                message = message.replace(map.getLeft(), map.getRight());
            }
        }

        if (key.contains("colors")) {
            return message;
        }

        return C.colorize(message);
    }

    public static void add(HashMap<String, Object> map) {
        HashMap<String, Object> map2 = new HashMap<>();

        for (String key : map.keySet()) {
            map2.put("messages." + key, map.get(key));
        }

        getMessageCache().putAll(map2);
        update();
    }

    public static void update() {
        messageFile.set(getMessageCache());
    }

    public void init(YMLFile messagefile) {
        messageFile = messagefile;
        FileConfiguration yml = messageFile.get();

        if (yml.contains("messages")) {
            for (String section : yml.getConfigurationSection("messages").getKeys(false)) {
                String data = yml.getString("messages." + section);
                if (data != null && !data.equals("") && !data.contains("MemorySection")) {
                    getMessageCache().put("messages." + section, data);
                }
            }
        } else {
            messagefile.set(new HashMap<String, Object>() {{
                put("messages.xfunbackend.info", "DEFAULT");
            }});

            init(messagefile);
        }
    }
}
