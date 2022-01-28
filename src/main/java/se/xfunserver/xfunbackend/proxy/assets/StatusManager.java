package se.xfunserver.xfunbackend.proxy.assets;

import lombok.Getter;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
import se.xfunserver.xfunbackend.proxy.files.ProxyYMLFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusManager extends ProxyModule {

    @Getter
    private final Map<String, Object> settings = new HashMap<>();
    private final ProxyYMLFile file;

    public StatusManager(Core plugin) {
        super(plugin, "Status Manager", true);

        this.file = new ProxyYMLFile(getPlugin(), getPlugin().getDataFolder(), "status_settings");

        this.loadData();

        this.getPlugin().getProxy().getScheduler().schedule(getPlugin(),
                this::sendGetRequest,
                (Integer) settings.get("time"), (Integer) settings.get("time"), TimeUnit.SECONDS);
    }

    public void loadData() {
        if (this.file.getConfiguration().contains("settings")) {
            this.settings.clear();
            this.settings.put("enabled", this.file.getConfiguration().getBoolean("settings.enabled"));
            this.settings.put("url", this.file.getConfiguration().getString("settings.url"));
            this.settings.put("time", this.file.getConfiguration().getInt("settings.time"));
        }
    }

    @Override
    public void reload() {
        loadData();
    }

    @Override
    public void deInit() {

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
}
