package se.xfunserver.xfunbackend.proxy.plugin_messaging.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import se.xfunserver.xfunbackend.assets.PluginMessageType;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.assets.object.xFunPMEvent;

public class PluginMessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e){
        if (e.getData().length <= 0){
            return;
        }
        try{
            ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
            ProxiedPlayer proxiedPlayer = Core.getCore().getProxy().getPlayer(e.getReceiver().toString());
            ProxiedPlayer targetPlayer = null;

            String target = in.readUTF();
            if (!target.equals("")){
                targetPlayer = Core.getCore().getProxy().getPlayer(target);
            }
            String type = in.readUTF();
            String content = in.readUTF();

            PluginMessageType pluginMessageType;
            try{
                pluginMessageType = PluginMessageType.valueOf(type.toUpperCase());
            }catch (Exception exception){
                return;
            }

            String[] contentArray = content.split("//");

            Core.getCore().getProxy().getPluginManager().callEvent(
                    new xFunPMEvent(pluginMessageType, proxiedPlayer, targetPlayer, contentArray));
        }catch (Exception ignored){}
    }

}
