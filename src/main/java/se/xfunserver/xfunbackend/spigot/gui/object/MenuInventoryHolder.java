package se.xfunserver.xfunbackend.spigot.gui.object;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import se.xfunserver.xfunbackend.spigot.gui.Gui;
import se.xfunserver.xfunbackend.spigot.gui.GuiElement;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

@Getter
public class MenuInventoryHolder implements InventoryHolder {

    private final xFunUser viewer;
    private Inventory inventory;

    private final Gui menu;
    private final boolean openParent;

    @Setter
    private int pageNumber;

    @Setter
    private boolean transitingPageState;

    private IntObjectMap<GuiElement> paginatedItems;

    public MenuInventoryHolder(xFunUser viewer, Gui menu){
        this.viewer = viewer;
        this.menu = menu;
        openParent = true;
    }

    public void setInventory(Inventory inventory){
        Validate.isTrue(this.inventory == null, "Inventory is already set");
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void createPaginatedItems() {
        paginatedItems = new IntObjectHashMap<>();
    }
}