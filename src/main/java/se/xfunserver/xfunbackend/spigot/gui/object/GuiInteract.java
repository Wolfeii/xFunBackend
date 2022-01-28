package se.xfunserver.xfunbackend.spigot.gui.object;


import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import java.util.Map;

public abstract class GuiInteract {

    @Getter
    private final Core plugin;
    @Getter
    private final String name;
    @Getter
    private final Map<GuiInteractElement, Integer> elements;

    public GuiInteract(Core plugin) {
        this(plugin, true);
    }

    public GuiInteract(Core plugin, boolean setup) {
        this.plugin = plugin;
        this.name = "Spelarens Inventory";
        this.elements = Maps.newHashMap();

        plugin.getGUIManager().getInteractMenus().add(this);

        if (setup)
            setup();
    }

    public abstract void setup();

    public void insert(Inventory inventory, xFunUser user) {
        for (Map.Entry<GuiInteractElement, Integer> element : elements.entrySet()) {
            inventory.setItem(element.getValue(), element.getKey().getIcon(user));
        }
    }

    public void addElement(GuiInteractElement menuElement, int slot) {
        if (slot >= 0) {
            if (!this.elements.containsValue(slot)) this.elements.put(menuElement, slot);
        }
    }
}
