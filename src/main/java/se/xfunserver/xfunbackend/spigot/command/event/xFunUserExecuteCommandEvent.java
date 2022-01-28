package se.xfunserver.xfunbackend.spigot.command.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import se.xfunserver.xfunbackend.spigot.command.xFunCommand;
import se.xfunserver.xfunbackend.spigot.listeners.xFunEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

@RequiredArgsConstructor
@Getter
public class xFunUserExecuteCommandEvent extends xFunEvent implements Cancellable {

    private boolean cancelled;
    private final xFunCommand command;
    private final xFunUser user;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
