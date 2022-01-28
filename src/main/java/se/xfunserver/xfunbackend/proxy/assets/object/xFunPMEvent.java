package se.xfunserver.xfunbackend.proxy.assets.object;


import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import se.xfunserver.xfunbackend.assets.PluginMessageType;

import javax.annotation.Nullable;

public class xFunPMEvent extends Event {

    @Getter
    private PluginMessageType type;
    @Getter
    private String[] data;
    @Getter
    private ProxiedPlayer receiver;
    @Getter
    @Nullable
    private ProxiedPlayer target;

    public xFunPMEvent(PluginMessageType type, ProxiedPlayer receiver, ProxiedPlayer target, String[] content) {
        this.type = type;
        this.receiver = receiver;
        this.target = target;
        this.data = content;
    }
}