package se.xfunserver.xfunbackend.proxy.user.object;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.spigot.assets.C;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class ProxyUser {

    private final UUID uuid;
    private final Rank rank;

    @Setter
    private List<UUID> friends;

    public ProxyUser(UUID uuid, Rank rank) {
        this.uuid = uuid;
        this.rank = rank;
        this.friends = Lists.newArrayList();
    }

    public ProxiedPlayer getProxiedPlayer() {
        return Core.getCore().getProxy().getPlayer(this.uuid);
    }

    public boolean hasFriend(ProxyUser proxyUser) {
        return proxyUser.getFriends().contains(this.uuid);
    }

    public final String prefix = "&3&lXFUN &8» &7";
    public void sendMessage(String... messages) {
        ProxiedPlayer proxiedPlayer = getProxiedPlayer();
        Arrays.stream(messages).forEach(s -> proxiedPlayer.sendMessage(TextComponent.fromLegacyText(C.colorize(prefix + s))));
    }
}
