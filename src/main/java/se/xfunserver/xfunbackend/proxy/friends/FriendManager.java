package se.xfunserver.xfunbackend.proxy.friends;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
import se.xfunserver.xfunbackend.proxy.friends.object.FriendRequest;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;
import se.xfunserver.xfunbackend.services.sql.SQLDataType;
import se.xfunserver.xfunbackend.services.sql.SQLDefaultType;
import se.xfunserver.xfunbackend.services.sql.TableBuilder;
import se.xfunserver.xfunbackend.services.sql.UtilSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class FriendManager extends ProxyModule {

    private final Map<ProxyUser, List<FriendRequest>> friendRequests;
    private final Map<ProxyUser, List<UUID>> friendsToUpdate;

    public FriendManager(Core plugin) {
        super(plugin, "Friend Manager", false);

        new TableBuilder("friends", getPlugin().getDatabaseManager())
                .addColumn("uuid", SQLDataType.BINARY, 16, false, SQLDefaultType.NO_DEFAULT, false)
                .addColumn("friend", SQLDataType.BINARY, 16, false, SQLDefaultType.NO_DEFAULT, false)
                .setConstraints("uuid", "friend")
                .execute();

        this.friendRequests = Maps.newHashMap();
        this.friendsToUpdate = Maps.newHashMap();
    }

    @Override
    public void reload() {

    }

    @Override
    public void deInit() {

    }

    public void addFriend(ProxyUser proxyUser, ProxyUser targetUser) {
        List<UUID> friends = this.friendsToUpdate.getOrDefault(proxyUser, Lists.newArrayList());
        friends.add(targetUser.getUuid());

        this.friendsToUpdate.put(proxyUser, friends);

        proxyUser.getFriends().add(targetUser.getUuid());
    }

    public FriendRequest getRequestByUser(ProxyUser proxyUser, ProxyUser targetUser) {
        if (this.friendRequests.containsKey(targetUser)) {
            for (FriendRequest friendRequest : friendRequests.get(targetUser)) {
                if (friendRequest.getTargetUser().getUuid().equals(proxyUser.getUuid())) {
                    return friendRequest;
                }
            }
        }
        return null;
    }

    public void addFriendRequest(FriendRequest friendRequest) {
        List<FriendRequest> friendRequests = this.friendRequests.getOrDefault(
                friendRequest.getSenderUser(), Lists.newArrayList());

        friendRequests.add(friendRequest);
        this.friendRequests.put(friendRequest.getSenderUser(), friendRequests);
    }

    public List<UUID> getFriendsForUser(UUID uuid) {
        List<UUID> friends = Lists.newArrayList();

        try {
            ResultSet resultSet = getPlugin().getDatabaseManager().getResults(
                    "friends", "uuid=?", new HashMap<Integer, Object>() {{
                        put(1, UtilSQL.convertUniqueId(uuid));
                    }}
            );

            if (resultSet.next()) {
                friends.add(UtilSQL.convertBinaryStream(resultSet.getBinaryStream("friend")));
            }

            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friends;
    }

    public void updateFriendsForUser(ProxyUser proxyUser) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
            this.friendsToUpdate.getOrDefault(proxyUser, Lists.newArrayList()).forEach(uuid -> {
                getPlugin().getDatabaseManager().insert("friends", new HashMap<String, Object>() {{
                    put("uuid", UtilSQL.convertUniqueId(proxyUser.getUuid()));
                    put("friend", UtilSQL.convertUniqueId(uuid));
                }});
            });
        });
    }


}
