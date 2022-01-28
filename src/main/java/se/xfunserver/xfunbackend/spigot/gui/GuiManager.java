package se.xfunserver.xfunbackend.spigot.gui;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.gui.object.GuiInteract;
import se.xfunserver.xfunbackend.spigot.gui.object.GuiInteractElement;
import se.xfunserver.xfunbackend.spigot.gui.object.InteractItem;
import se.xfunserver.xfunbackend.spigot.gui.object.MenuInventoryHolder;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;

import java.util.List;

public class GuiManager extends Module {

    @Getter
    private final List<GuiInteract> interactMenus = Lists.newArrayList();

    public GuiManager(Core plugin) {
        super(plugin, "Gui Manager", false);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        xFunLogger.debug("GUIMANAGER CLICK -1");

        if (topInventory.getHolder() instanceof MenuInventoryHolder) {
            MenuInventoryHolder holder = (MenuInventoryHolder) topInventory.getHolder();
            Gui menu = holder.getMenu();
            xFunLogger.debug("GUIMANAGER CLICK -2");
            if (event.getRawSlot() == event.getSlot()) {
                int slot = event.getSlot();
                xFunLogger.debug("GUIMANAGER CLICK 0");
                if (menu instanceof PaginatedGui) {
                    xFunLogger.debug("GUIMANAGER CLICK 1");

                    boolean navigational = false;
                    int newPage = holder.getPageNumber();
                    if (slot == ((PaginatedGui) menu).getChangePageSlots().getLeft()) {
                        newPage--;
                        navigational = true;
                    } else if (slot == ((PaginatedGui) menu).getChangePageSlots().getRight()) {
                        newPage++;
                        navigational = true;
                    }
                    xFunLogger.debug("GUIMANAGER CLICK 2");
                    event.setCancelled(true);

                    if (newPage >= 1) {
                        ((PaginatedGui) menu).setPage(holder.getViewer(), newPage);
                    }
                    xFunLogger.debug("GUIMANAGER CLICK 3");
                    if (navigational) {
                        return;
                    }
                }

                GuiElement item = menu.getElement(slot);
                xFunLogger.debug("GUIMANAGER CLICK 4");

                if (item == null && holder.getPaginatedItems() != null) {
                    xFunLogger.debug("GUIMANAGER CLICK 5");
                    item = holder.getPaginatedItems().get(slot);
                }

                xFunLogger.debug("GUIMANAGER CLICK 6");

                boolean onInsert = menu.onInsert(event.getCurrentItem(), topInventory);

                if (item != null) {
                    xFunLogger.debug("GUIMANAGER CLICK 7");
                    if (!item.disableClickEvent()) {
                        xFunLogger.debug("GUIMANAGER CLICK 8");
                        if (onInsert) {
                            xFunLogger.debug("GUIMANAGER CLICK 9");
                            event.setCancelled(false);
                        } else {
                            xFunLogger.debug("GUIMANAGER CLICK 10");
                            event.setCancelled((event.getRawSlot() == event.getSlot() || event.isShiftClick()
                                    || event.getClick() == ClickType.DOUBLE_CLICK));
                        }
                    }
                    menu.setIgnoringParent(true);
                    if (!item.disableClickEvent()) {
                        item.click(holder.getViewer(), event.getClick(), menu);
                    }
                    menu.setIgnoringParent(false);
                }else {
                    event.setCancelled(!onInsert);
                }
            }else{
                if (menu.onInsert(event.getCurrentItem(), topInventory)) {
                    xFunLogger.debug("GUIMANAGER CLICK 11");
                    event.setCancelled(false);
                } else {
                    xFunLogger.debug("GUIMANAGER CLICK 12");
                    event.setCancelled((event.getRawSlot() == event.getSlot() || event.isShiftClick()
                            || event.getClick() == ClickType.DOUBLE_CLICK));
                }
            }
        }
    }

    @EventHandler
    public void on(InventoryDragEvent event){
        if (event.getInventory().getHolder() instanceof MenuInventoryHolder){
            xFunLogger.debug("GUIMANAGER DRAG 2");
            if (event.getOldCursor() != null) {
                if (event.getInventory() == event.getView().getTopInventory()) {
                    Gui gui = ((MenuInventoryHolder) event.getInventory().getHolder()).getMenu();
                    xFunLogger.debug("GUIMANAGER DRAG 3");
                    boolean onInsert = gui.onInsert(event.getOldCursor(), event.getInventory());
                    event.setCancelled(!onInsert);
                    xFunLogger.debug(onInsert + " = GUIMANAGER DRAG 4");
                }
            }
        }
    }

    @EventHandler
    public void on(InventoryMoveItemEvent event){
        Inventory destination = event.getDestination();
        xFunLogger.debug("ON INSERT GUI 1");
        if (destination instanceof Gui) {
            boolean onInsert = ((Gui) destination).onInsert(event.getItem(), destination);
            xFunLogger.debug("ON INSERT GUI 2");
            xFunLogger.debug(onInsert + " = ON INSERT GUI 2");
            event.setCancelled(!onInsert);
            event.setItem(onInsert ? event.getItem() : null);
        }
    }

	/*
		Player player = (Player) event.getWhoClicked();
		xFunUser user = Core.getPlugin().getUserManager().getUser(player);
		int slot = event.getSlot();
		for(Gui menu : Lists.newArrayList(menus)) {
			if(menu.getName().equalsIgnoreCase("Player Inventory")||
				(event.getView().getTitle().equals(menu.getName())&&menu.getCurrentSessions().containsKey(player.getUniqueId()))) {
				if(event.getCurrentItem() != null) {
					if (!menu.onInsert(event.getCurrentItem())){
						event.setCancelled(true);
					}
					if(menu.getElements().containsKey(slot)) {
						menu.getElements().get(slot).click(user, event.getClick());
					}
				}
			}
		}
	 */

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        xFunUser user = Core.getPlugin().getUserManager().getUser(player);
        for(GuiInteract menu : Lists.newArrayList(interactMenus)) {
            if (e.getItem() != null && e.getItem().hasItemMeta()){
                for(GuiInteractElement guiInteractElement : menu.getElements().keySet()) {
                    if (guiInteractElement.getIcon(user).equals(e.getItem())) {
                        guiInteractElement.click(user, e.getAction());
                        //TODO: Add a click cooldown to prevent spamming
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent e){
        if (e.getAction() != Action.PHYSICAL){
            for (InteractItem interactItem : InteractItem.getInteractItems()) {
                if (interactItem.isSimilar(e.getItem())){
                    xFunUser user = Core.getPlugin().getUserManager().getUser(e.getPlayer());
                    e.setCancelled(true);
                    InteractItem.getInteractItems().remove(interactItem);
                    interactItem.onClick(user, e.getAction());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        xFunUser user = Core.getPlugin().getUserManager().getUser(e.getPlayer().getUniqueId());
        InventoryView view = e.getView();
        Inventory topInventory = view.getTopInventory();

        if (topInventory.getHolder() instanceof MenuInventoryHolder) {
            MenuInventoryHolder holder = (MenuInventoryHolder) topInventory.getHolder();
            Gui parent = holder.getMenu().getParent();
            holder.getMenu().getCurrentSessions().remove(user.getUuid());
            if (!holder.getMenu().onClose(topInventory, user)){
                return;
            }
            if (parent != null && !holder.getMenu().isIgnoringParent()) {
                getPlugin().getRunnableManager().runTaskLater("open parent gui", core ->
                        parent.open(holder.getViewer()), 1L);
            }
        }
    }

    @Override
    public void reload(String response) {

    }
}
