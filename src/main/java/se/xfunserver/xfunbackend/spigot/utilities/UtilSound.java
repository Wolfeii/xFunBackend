package se.xfunserver.xfunbackend.spigot.utilities;

import org.bukkit.Sound;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public class UtilSound {

    private xFunUser user;

    public UtilSound(xFunUser user){
        this.user = user;
    }

    public void pling(){
        user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
    }

    public void deepPling(){
        user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2, 2);
    }

    public void play(Sound sound){
        user.getPlayer().playSound(user.getPlayer().getLocation(), sound, 2, 2);
    }

}
