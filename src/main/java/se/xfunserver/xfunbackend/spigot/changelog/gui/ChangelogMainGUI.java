package se.xfunserver.xfunbackend.spigot.changelog.gui;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.changelog.object.Changelog;
import se.xfunserver.xfunbackend.spigot.changelog.object.ChangelogType;
import se.xfunserver.xfunbackend.spigot.gui.Gui;
import se.xfunserver.xfunbackend.spigot.gui.GuiElement;
import se.xfunserver.xfunbackend.spigot.gui.object.GuiSize;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;
import se.xfunserver.xfunbackend.spigot.utilities.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ChangelogMainGUI extends Gui {

    @Getter
    private ChangelogType changelogType;

    public ChangelogMainGUI(Core plugin) {
        super(plugin, "xFun Changelog", GuiSize.FOUR_ROWS);
    }

    @Override
    public void setup() {
        surroundWith(new GuiElement() {
            @Override
            public ItemStack getIcon(xFunUser synergyUser) {
                return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .setName(" ")
                        .build();
            }

            @Override
            public void click(xFunUser player, ClickType clickType, Gui gui) {}
        });

        for (Changelog changelog : getPlugin().getChangelogManager().getChangelogs()) {
            int place = getCenterInput().get(getPlugin().getChangelogManager().getChangelogs().indexOf(changelog));
            addElement(place, new GuiElement() {
                @Override
                public ItemStack getIcon(xFunUser synergyUser) {
                    return new ItemBuilder(Material.MAP)
                            .setName(C.colorize(changelog.getTitle()))
                            .addLore(" ")
                            .build();
                }

                @Override
                public void click(xFunUser player, ClickType clickType, Gui gui) {
                    changelog.open(player.getPlayer());
                }
            });
        }
    }
}
