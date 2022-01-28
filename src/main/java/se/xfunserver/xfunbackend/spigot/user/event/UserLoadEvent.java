package se.xfunserver.xfunbackend.spigot.user.event;

import lombok.Getter;
import lombok.Setter;
import se.xfunserver.xfunbackend.spigot.listeners.xFunEvent;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

@Getter
public class UserLoadEvent extends xFunEvent {

    private final xFunUser user;
    private final UserLoadType loadType;

    public UserLoadEvent(xFunUser user, UserLoadType loadType) {
        this.user = user;
        this.loadType = loadType;
    }

    public enum UserLoadType {
        NEW,
        RETRIEVED_FROM_DATABASE,
        NEW_INSTANCE
    }
}
