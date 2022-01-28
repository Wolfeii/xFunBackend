package se.xfunserver.xfunbackend.spigot.assets.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.gui.Gui;
import se.xfunserver.xfunbackend.spigot.gui.GuiElement;
import se.xfunserver.xfunbackend.spigot.gui.object.GuiSize;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;
import se.xfunserver.xfunbackend.spigot.utilities.ItemBuilder;

public abstract class ConfirmationGUI extends Gui {

    public ConfirmationGUI(Core plugin, String name) {
        super(plugin, name, GuiSize.THREE_ROWS);
    }

    public abstract void onAccept(xFunUser user);
    public abstract void onDisallow(xFunUser user);

    @Override
    public void setup() {
        for (int i : getLeftSide()) {
            addElement(i, new GuiElement() {
                @Override
                public ItemStack getIcon(xFunUser synergyUser) {
                    return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                            .setName("&a&lJa")
                            .build();
                }

                @Override
                public void click(xFunUser player, ClickType clickType, Gui gui) {
                    player.getPlayer().closeInventory();
                    onAccept(player);
                }
            });
        }

        for (int i : getRightSide()) {
            addElement(i, new GuiElement() {
                @Override
                public ItemStack getIcon(xFunUser synergyUser) {
                    return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                            .setName("&c&lNej")
                            .build();
                }

                @Override
                public void click(xFunUser player, ClickType clickType, Gui gui) {
                    player.getPlayer().closeInventory();
                    onDisallow(player);
                }
            });
        }

        for (int i : getMiddle()) {
            addElement(i, new GuiElement() {
                @Override
                public ItemStack getIcon(xFunUser synergyUser) {
                    return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setName(" ")
                            .build();
                }

                @Override
                public void click(xFunUser player, ClickType clickType, Gui gui) {
                }
            });
        }
    }

    private int[] getLeftSide(){
        return new int[]{0,1,2,3,9,10,11,12,18,19,20,21};
    }

    private int[] getRightSide(){
        return new int[]{5,6,7,8,14,15,16,17,23,24,25,26};
    }

    private int[] getMiddle(){
        return new int[]{4,13,22};
    }
}
