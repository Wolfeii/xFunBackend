package se.xfunserver.xfunbackend.spigot.user;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.services.sql.UtilSQL;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.user.event.UserLoadEvent;
import se.xfunserver.xfunbackend.spigot.user.listener.UserJoinListener;
import se.xfunserver.xfunbackend.spigot.user.listener.UserQuitListener;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class UserManager extends Module {

    @Getter
    private final Map<UUID, xFunUser> users = Maps.newHashMap();

    public UserManager(Core plugin) {
        super(plugin, "User Manager", false);

        registerListeners(
                new UserJoinListener(),
                new UserQuitListener()
        );

    }

    @Override
    public void reload(String response) {

    }

    public Collection<xFunUser> getOnlineUsers() {
        if (this.users != null && !this.users.isEmpty()) {
            return this.users.values();
        }
        return Collections.emptyList();
    }

    public xFunUser getFakeUser(String name) {
        xFunUser user = this.getUser(name, false);
        if (user != null) {
            return user;
        }
        return retrievePlayer(loadFromUsername(name), false);
    }

    public xFunUser getUser(String name, Boolean database) {
        for (xFunUser user : users.values()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        if (database) {
            return retrievePlayer(loadFromUsername(name), true);
        }
        return null;
    }

    public xFunUser getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public xFunUser getUser(UUID uuid) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        }
        return null;
    }

    public ResultSet loadFromUUID(UUID uuid) {
        try {
            return Core.getPlugin().getDatabaseManager().getResults(
                    "users ", "uuid=?", new HashMap<Integer, Object>() {{
                        put(1, UtilSQL.convertUniqueId(uuid));
                    }}
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet loadFromUsername(String name) {
        try {
            return Core.getPlugin().getDatabaseManager().getResults(
                    "users ", "name=?", new HashMap<Integer, Object>() {{
                        put(1, name);
                    }}
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public xFunUser retrievePlayer(ResultSet resultSet, boolean save) {
        if (resultSet != null) {
            final xFunUser user;
            Rank rank = Rank.NONE;
            Timestamp joinedOn;

            try {
                if (resultSet.next()) {
                    xFunLogger.info("Startar laddnings processen för en användare...");

                    UUID uuid = UtilSQL.convertBinaryStream(resultSet.getBinaryStream("uuid"));
                    joinedOn = resultSet.getTimestamp("joined_on");

                    xFunLogger.debug("Hittade följande UUID (" + uuid + ") som anslöt första gången vid: " + joinedOn);
                    if (Rank.fromName(resultSet.getString("rank")) != null) {
                        xFunLogger.debug("Användaren har en rank associerat med sitt konto.");
                        rank = Rank.fromName(resultSet.getString("rank"));
                    }

                    user = new xFunUser(
                            uuid,
                            resultSet.getString("name"),
                            rank,
                            UserLoadEvent.UserLoadType.RETRIEVED_FROM_DATABASE,
                            save
                    );

                    user.setJoinedOn(joinedOn);
                    return user;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void cacheExistingPlayers() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            new xFunUser(p.getUniqueId(), p.getName(), Rank.NONE);
        }
    }

    public void updateUserTable(xFunUser user) {
        Core.getPlugin().getRunnableManager().runTaskAsynchronously(
                "Uppdatera User",
                core -> {
                    HashMap<String, Object> data = new HashMap<String, Object>() {{
                        put("uuid", UtilSQL.convertUniqueId(user.getUuid()));
                        put("name", user.getName());
                        put("rank", user.getRank().toString().toUpperCase());
                        put("joined_on", user.getJoinedOn());
                        put("user_experience", user.getUserExperience().toString().toUpperCase());
                    }};

                    xFunLogger.info("Skickar följande request, " + data);
                    if (!getPlugin().getDatabaseManager().insert("users", data)) {
                        Core.getPlugin().getDatabaseManager().update(
                                "users",
                                data,
                                new HashMap<String, Object>() {{
                                    put("uuid", UtilSQL.convertUniqueId(user.getUuid()));
                                }}
                        );
                    }
                }
        );
    }
}
