package se.xfunserver.xfunbackend.spigot.changelog;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.changelog.command.CommandChangelog;
import se.xfunserver.xfunbackend.spigot.changelog.listeners.ChangelogJoinListener;
import se.xfunserver.xfunbackend.spigot.changelog.object.Changelog;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChangelogManager extends Module {

    @Getter
    private List<Changelog> changelogs = Lists.newArrayList();

    public ChangelogManager(Core plugin) {
        super(plugin, "Changelog Manager", true);

        registerCommand(
                new CommandChangelog(getPlugin())
        );

        registerListeners(
                new ChangelogJoinListener()
        );

        try {
            getPlugin().getPluginManager().getFileStructure().getYMLFile("example_changelog").set(
                    new HashMap<String, Object>() {{
                        put("changelog.title", "&bUppdateringar från &3XFUN");
                        put("changelog.date", "18-01-2022");
                        put("changelog.time", "20:00");
                        put("changelog.show_till", "20-01-2022");
                        put("changelog.changelog", new ArrayList<String>() {{
                            add("Detta är första sidan");
                            add("Detta är andra sidan");
                            add("Detta är tredje sidan");
                        }});
                    }}
            );
        } catch (FileNotFoundException e) {
            Sam.getRobot().error(this, e.getMessage(), "Hittade inte filen för exempel changelog, lägg till den.", e);
        }

        createCachedChangelogs();
    }

    @Override
    public void reload(String response) {
        createCachedChangelogs();
    }

    public void addChangelog(Changelog changelog) {
        this.changelogs.add(changelog);
    }

    public Changelog getLatestChangelog() {
        for(Changelog changelog : getChangelogs()) {
            if (new Date().before(changelog.getShowTill())) {
                return changelog;
            }
        }
        return null;
    }

    public void createCachedChangelogs() {
        File dataFolder = new File(getPlugin().getDataFolder(),
                "changelogs" + File.separator + "server");
        this.changelogs.clear();

        File[] changelogFiles = dataFolder.listFiles();
        if (changelogFiles != null && changelogFiles.length > 0) {
            try {
                for (File file : changelogFiles) {
                    FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

                    String title;
                    Date date, showTill;

                    String time = configuration.getString("changelog.time");

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    date = format.parse(configuration.getString("changelog.date") + " " + time);

                    showTill = new SimpleDateFormat("dd-MM-yyyy")
                            .parse(configuration.getString("changelog.show_till"));

                    title = configuration.getString("changelog.title").replace("%date%",
                            new SimpleDateFormat("MMM dd, yyyy - HH:mm").format(date));

                    List<String> pages = Lists.newArrayList();
                    configuration.getStringList("changelog.changelog").forEach(page -> {
                        String colorizedPage = C.colorize(page);
                        pages.add(colorizedPage);
                    });

                    new Changelog(title, date, showTill, pages);
                }
            } catch (Exception e) {
                Sam.getRobot().error(this, e.getMessage(), "En del från en changelog fattades, se över alla changelogs", e);
            }
        }
    }
}
