package se.xfunserver.xfunbackend.spigot.user.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.spigot.listeners.xFunEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

@RequiredArgsConstructor
@Getter
public class UserQuitEvent extends xFunEvent {

    private final xFunUser user;
    private final Player playerObject;
}
