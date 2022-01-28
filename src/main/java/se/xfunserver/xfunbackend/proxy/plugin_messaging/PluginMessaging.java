package se.xfunserver.xfunbackend.proxy.plugin_messaging;

import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.ProxyModule;
import se.xfunserver.xfunbackend.proxy.plugin_messaging.listener.PluginMessageListener;

public class PluginMessaging extends ProxyModule {

    private String[] channels = new String[] {
            "BungeeCord"
    };

    public PluginMessaging(Core plugin) {
        super(plugin, "PluginMessage Manager", false);

        registerListeners(
                new PluginMessageListener()
        );

        registerChannels();
    }

    @Override
    public void reload() {

    }

    @Override
    public void deInit() {

    }

    public void registerChannels(){
        for(String channel : this.channels){
            getPlugin().getProxy().registerChannel(channel);
        }
    }
}
