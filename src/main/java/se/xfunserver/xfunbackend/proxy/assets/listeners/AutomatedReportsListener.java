package se.xfunserver.xfunbackend.proxy.assets.listeners;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import se.xfunserver.xfunbackend.assets.PluginMessageType;
import se.xfunserver.xfunbackend.discord.Discord;
import se.xfunserver.xfunbackend.proxy.assets.object.xFunPMEvent;

public class AutomatedReportsListener implements Listener {

    @EventHandler
    public void onIncomingPM(xFunPMEvent event) {
        if (event.getType() == PluginMessageType.AUTOMATED_ERROR) {
            String fileName = event.getData()[0];
            String exceptionMessage = event.getData()[1];
            String exception = event.getData()[2];

            Discord.getDiscordManager().automatedError(exceptionMessage, exception,
                    "Funktion kommer senare..."
            );
        }
    }
}
