package se.xfunserver.xfunbackend.spigot.gui;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

public abstract class GuiElement {

    public abstract ItemStack getIcon(xFunUser synergyUser);

    public abstract void click(xFunUser player, ClickType clickType, Gui gui);

    public boolean disableClickEvent(){return false;}

}
