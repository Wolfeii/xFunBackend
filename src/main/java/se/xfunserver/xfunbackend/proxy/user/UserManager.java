package se.xfunserver.xfunbackend.proxy.user;

import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.apache.logging.log4j.io.ByteStreamLogger;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager extends ProxyModule {

    @Getter
    private final Map<UUID, ProxyUser> proxyUsers = new HashMap<>();

    public UserManager(Core plugin) {
        super(plugin, "User Manager", false);
    }

    @Override
    public void reload() {

    }

    @Override
    public void deInit() {

    }

    public ProxyUser getUser(UUID uuid) {
        return this.proxyUsers.get(uuid);
    }

    public ProxyUser getUser(String name) {
        for (ProxyUser proxyUser : this.proxyUsers.values()) {
            if (proxyUser.getProxiedPlayer().getName().equalsIgnoreCase(name)) {
                return proxyUser;
            }
        }
        return null;
    }

    public InputStream convertUniqueId(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }

    public UUID convertBinaryStream(InputStream stream) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        try {
            byteBuffer.put(ByteStreams.toByteArray(stream));
            byteBuffer.flip();
            return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
        } catch (IOException e) {
            xFunLogger.error(e.getMessage());
        }
        return null;
    }
}
