package se.xfunserver.xfunbackend.spigot.user.event;

import lombok.Getter;
import se.xfunserver.xfunbackend.spigot.listeners.xFunEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class UserPreLoadEvent extends xFunEvent {

    @Getter
    private final xFunUser user;

    public UserPreLoadEvent(xFunUser user) {
        this.user = user;
    }
}
