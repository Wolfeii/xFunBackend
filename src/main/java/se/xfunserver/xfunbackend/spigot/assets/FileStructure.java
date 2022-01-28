package se.xfunserver.xfunbackend.spigot.assets;

import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.files.json.JSONFile;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class FileStructure {

    private final Map<String, YMLFile> ymlFiles = new HashMap<>();
    private final Map<String, JSONFile> jsonFiles = new HashMap<>();

    public enum FileType {
        JSON, YML
    }

    public YMLFile getYMLFile(String key) throws FileNotFoundException {
        if (ymlFiles.containsKey(key)) {
            return ymlFiles.get(key);
        }

        throw new FileNotFoundException("Hittade inte YML filen som du söker med key '" + key + "'");
    }

    public JSONFile getJSONFile(String key) throws FileNotFoundException {
        if (jsonFiles.containsKey(key)) {
            return jsonFiles.get(key);
        }

        throw new FileNotFoundException("Hittade inte JSON filen som du söker med key '" + key + "'");
    }

    public FileStructure add(String key, String folder, String file, FileType type) {
        String query;
        if (folder == null) {
            query = Core.getPlugin().getDataFolder() + "";
        } else {
            query = Core.getPlugin().getDataFolder() + File.separator + folder;
        }

        switch (type) {
            case YML:
                if (ymlFiles.containsKey(key)) {
                    return this;
                }

                ymlFiles.put(key, new YMLFile(query, file));
                break;
            case JSON:
                if (jsonFiles.containsKey(key)) {
                    return this;
                }

                jsonFiles.put(key, new JSONFile((folder == null ? "" : folder), file));
                break;
        }
        return this;
    }

    public void save() {
        for (YMLFile files : ymlFiles.values()) {
            if (!files.exists())
                files.save();
        }

        for (JSONFile files : jsonFiles.values()) {
            if (!files.exists())
                files.finish();
        }
    }
}
