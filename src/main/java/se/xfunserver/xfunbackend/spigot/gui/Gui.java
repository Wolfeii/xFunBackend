package se.xfunserver.xfunbackend.spigot.gui;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.gui.object.GuiSize;
import se.xfunserver.xfunbackend.spigot.gui.object.MenuInventoryHolder;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;
import se.xfunserver.xfunbackend.spigot.utilities.ItemBuilder;

import java.util.*;

public abstract class Gui {

    @Getter
    private final Core plugin;
    private final String name;
    private final GuiSize guiSize;
    @Getter
    private Gui parent = null;
    @Setter
    @Getter
    private boolean ignoringParent;
    private final Map<Integer, GuiElement> elements;
    private final Map<UUID, Inventory> currentSessions;

    public Gui(Core plugin, String name, GuiSize guiSize) {
        this(plugin, name, guiSize, true);
    }

    public Gui(Core plugin, String name, GuiSize guiSize, boolean setup) {
        this.plugin = plugin;
        this.name = name;
        this.guiSize = guiSize;
        this.elements = Maps.newHashMap();
        this.currentSessions = Maps.newHashMap();
        this.ignoringParent = false;

        if (setup)
            setup();
    }

    public abstract void setup();
    public boolean onClose(Inventory inventory, xFunUser user){
        return true;
    }
    public boolean onInsert(ItemStack itemStack, Inventory inventory){
        return false;
    }

    public void insert(Inventory inventory, xFunUser user){
        for(Map.Entry<Integer, GuiElement> element : elements.entrySet()) {
            inventory.setItem(element.getKey(), element.getValue().getIcon(user));
        }
    }

    /**
     * Opens the menu using a PlayerHolder, supports custom implementations for attaching data to the
     * player.
     *
     * @param player the holder to open the menu for.
     */
    public final void open(Player player) {
        xFunUser user = getPlugin().getUserManager().getUser(player);
        open(user);
    }

    /**
     * Opens the menu using a PlayerHolder, supports custom implementations for attaching data to the
     * player.
     *
     * @param user the holder to open the menu for.
     */
    public final void open(xFunUser user) {
        MenuInventoryHolder inventoryHolder = new MenuInventoryHolder(user, this);
        Inventory inventory = plugin.getServer()
                .createInventory(inventoryHolder, guiSize.getSlots(), name);

        inventoryHolder.setInventory(inventory);
        setItems(inventory, user);

        user.getPlayer().openInventory(inventory);

        Player player = user.getPlayer();

        currentSessions.remove(player.getUniqueId());
        currentSessions.put(player.getUniqueId(), inventory);
        player.updateInventory();
    }

    public void update(xFunUser user){
        user.getPlayer().updateInventory();

        Inventory inventory = currentSessions.get(user.getUuid());

        Validate.notNull(inventory, "Cannot update before opening inventory!");

        this.ignoringParent = false;
        elements.clear();

        setup();
        setItems(inventory, user);
    }

    void setItems(Inventory inventory, xFunUser user){
        elements.forEach((key, value) -> inventory.setItem(key, value.getIcon(user)));
    }

//	private void removeFromSession(Player player){
//		for(Gui menu : plugin.getGUIManager().getMenus()) {
//			if(!menu.equals(this)) {
//				menu.getCurrentSessions().remove(player.getUniqueId());
//			}
//		}
//	}

    public void close(Player player) {
        player.closeInventory();
        currentSessions.remove(player.getUniqueId());
    }


    public void close(Player player, boolean ignoringParent) {
        this.ignoringParent = ignoringParent;
        player.closeInventory();
        currentSessions.remove(player.getUniqueId());
    }

    public Gui setParent(Gui parent){
        this.parent = parent;
        return this;
    }

    public boolean hasParent(){
        return this.parent != null;
    }

    public Gui getOuterClazz(){
        return this;
    }

    public GuiElement getBackGuiElement(){
        return getBackGuiElement("??dG?? Tillbaka", new ItemBuilder(Material.AIR));
    }

    public GuiElement getBackGuiElement(ItemBuilder alternative){
        return getBackGuiElement("??cG?? Tillbaka", alternative);
    }

    public GuiElement getBackGuiElement(String itemName, ItemBuilder alternative){
        return new GuiElement() {
            @Override
            public ItemStack getIcon(xFunUser user) {
                if (!hasParent()){
                    return alternative.build();
                }
                return new ItemBuilder(Material.ARROW)
                        .setName("??7"+itemName)
                        .build();
            }

            @Override
            public void click(xFunUser user, ClickType clickType, Gui gui) {
                if (hasParent()) parent.open(user);
            }
        };
    }

    public void addElement(GuiElement element) {
        for (int i = 0; i < this.guiSize.getSlots(); ++i) {
            if (!this.elements.containsKey(i)) {
                this.addElement(i, element);
                return;
            }
        }
    }

    public void addUnsafeElement(int slot, GuiElement menuElement) {
        try{
            this.elements.put(slot, menuElement);
        }catch (Exception e){
            xFunLogger.error(e.getMessage());
        }
    }

    public void line(int first, int last, GuiElement element){
        for (int i=first;i<=last;i++){
            addElement(i, element);
        }
    }

    public boolean isFull() {
        for (int i = 0; i < this.guiSize.getSlots(); ++i) {
            if (this.getElement(i) == null) {
                return false;
            }
        }

        return true;
    }

    public void surroundWith(GuiElement item) {
        if (getSize() >= GuiSize.THREE_ROWS.getSlots()) {
            Integer[] walls = new Integer[]{9,17,18,26,27,35,36,44};
            List<Integer> slots = new ArrayList<>();
            final int size = this.guiSize.getSlots();

            // Outer walls
            int csize = size;
            for(int i=0;i<9;i++){
                slots.add(--csize);
                slots.add(i);
            }

            slots.addAll(Arrays.asList(walls));
            Object[] slotsArray = slots.toArray();
            Arrays.sort(slotsArray);

            for (Object obj : slotsArray) {
                int i = Integer.parseInt(obj.toString());

                if (i >= size){
                    break;
                }
                if (getElement(i) == null) {
                    addElement(i, item);
                }
            }
        }
    }

    public int getCenter(){
        switch (this.guiSize){
            case TWO_ROWS:
                return 4;
            case THREE_ROWS:
            case FOUR_ROWS:
                return 13;
            case FIVE_ROWS:
            case SIX_ROWS:
                return 22;
        }
        return 4;
    }

    public List<Integer> getCenterInput(){
        List<Integer> places = new ArrayList<>();

        switch (this.guiSize){
            case ONE_ROW:
                places.addAll(Arrays.asList(1,2,3,4,5,6,7));
                break;
            case TWO_ROWS:
                places.addAll(Arrays.asList(1,2,3,4,5,6,7,10,11,12,13,14,15,16));
                break;
            case THREE_ROWS:
                places.addAll(Arrays.asList(10,11,12,13,14,15,16));
                break;
            case FOUR_ROWS:
                places.addAll(Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25));
                break;
            case FIVE_ROWS:
                places.addAll(Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,32,33,34));
                break;
            case SIX_ROWS:
                places.addAll(Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,32,33,34,37,38,39,40,41,42,43));
                break;
        }
        return places;
    }

    public void addElement(int slot, GuiElement menuElement) {
        if (slot >= 0) {
            this.elements.put(slot, menuElement);
        }
    }

    public void removeElement(int slot) {
        this.elements.remove(slot);
    }

    public GuiElement getElement(int slot) {
        if(elements.containsKey(slot)) {
            return elements.get(slot);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return guiSize.getSlots();
    }

    public Map<Integer, GuiElement> getElements() {
        return elements;
    }

    public Map<UUID, Inventory> getCurrentSessions() {
        return currentSessions;
    }

}