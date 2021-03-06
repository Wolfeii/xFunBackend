package se.xfunserver.xfunbackend.spigot.files.yml;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import se.xfunserver.xfunbackend.assets.xFunLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class YMLFile {

    @Getter
    private File file;
    private FileConfiguration data;
    private String dataFolder;
    private String name;

    public YMLFile(String dataFolder, String fileName) {
        this.dataFolder = dataFolder;
        this.name = fileName;

        file = new File(dataFolder, fileName+".yml");
        data = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get() {
        return data;
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            xFunLogger.error(e.getMessage());
        }
    }

    public void reload() {
        file = new File(dataFolder, name+".yml");
        data = YamlConfiguration.loadConfiguration(file);
    }

    public YMLFile setHeader(String header){
        get().options().header(header);
        return this;
    }

    public void set(HashMap<String, Object> data) {
        if (!data.isEmpty()){
            for(String key : data.keySet()){
                if (key.startsWith("#")){
                    continue;
                }
                if (!get().contains(key)){
                    get().set(key, data.get(key));
                }
            }
        }
        save();
    }

    public boolean exists() {
        return file.exists();
    }

}