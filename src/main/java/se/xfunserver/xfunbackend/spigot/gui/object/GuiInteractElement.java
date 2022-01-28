package se.xfunserver.xfunbackend.spigot.gui.object;

import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public abstract class GuiInteractElement {

    public abstract ItemStack getIcon(xFunUser user);

    public abstract void click(xFunUser player, Action action);

}