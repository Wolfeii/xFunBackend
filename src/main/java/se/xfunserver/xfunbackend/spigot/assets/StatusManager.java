package se.xfunserver.xfunbackend.spigot.assets;


import lombok.Getter;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.files.yml.YMLFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusManager extends Module {

    @Getter
    private final Map<String, Object> settings = new HashMap<>();

    public StatusManager(Core plugin) {
        super(plugin, "Status Manager", true);

        this.loadData();

        this.getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(),
                this::sendGetRequest,
                (Integer) settings.get("time") * 20, (Integer) settings.get("time") * 20);
    }

    public void loadData() {
        try {
            YMLFile ymlFile = getPlugin().getPluginManager().getFileStructure().getYMLFile("status_settings");

            if (ymlFile.get().contains("settings")) {
                this.settings.clear();
                this.settings.put("enabled", ymlFile.get().getBoolean("settings.enabled"));
                this.settings.put("url", ymlFile.get().getString("settings.url"));
                this.settings.put("time", ymlFile.get().getInt("settings.time"));
            }
        } catch (Exception e) {
            xFunLogger.error(e.getMessage());
        }
    }


    public void sendGetRequest() {
        try {
            URL getUrl = new URL((String) this.settings.get("url"));
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                xFunLogger.debug(response.toString());
                in.close();
            } else {
                xFunLogger.error("Ett fel uppstod med att skicka GET request.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload(String response) {
        loadData();
    }
}
