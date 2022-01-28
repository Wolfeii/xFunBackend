package se.xfunserver.xfunbackend.spigot.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import se.xfunserver.xfunbackend.spigot.listeners.xFunEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import javax.annotation.Nullable;
import java.awt.*;

@RequiredArgsConstructor
@Getter
public class xFunUserChatEvent extends xFunEvent implements Cancellable {

    private boolean cancelled;
    private final xFunUser user;
    private final String message;
    private final String format;
    @Setter
    @Nullable
    private TextComponent formatComponent;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}