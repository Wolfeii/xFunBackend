package se.xfunserver.xfunbackend.spigot.utilities;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder extends ItemStack {

    public ItemMeta itemMeta;
    private List<String> lore;

    public ItemBuilder(final Material material) {
        this(material, 1);
    }

    public ItemBuilder(final ItemStack stack) {
        super(stack);

        initItemMeta();
        if (this.itemMeta.hasLore()) {
            this.lore = this.itemMeta.getLore();
        }
    }

    public ItemBuilder(final Material material, final int amount) {
        this(material, amount, (byte) 0);
    }

    public ItemBuilder(final Material material, final int amount, final short damage) {
        super(material, amount, damage);
    }

    public ItemBuilder setName(String displayName) {
        initItemMeta();
        String t = ChatColor.translateAlternateColorCodes('&', displayName);
        this.itemMeta.setDisplayName(ChatColor.RESET + t);
        this.itemMeta.setLocalizedName(ChatColor.RESET + t);
        return this;
    }

    private void initItemMeta() {
        this.initItemMeta(getItemMeta());
    }

    public void initItemMeta(ItemMeta itemMeta) {
        if (this.itemMeta == null) {
            this.itemMeta = itemMeta;
        }
    }

    @Deprecated
    public ItemBuilder setLore(String... lore) {
        addLore(lore);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        initItemMeta();
        List<String> modifiedLore = new ArrayList<>();
        for (String line : lore) {
            modifiedLore.add("§7" + ChatColor.translateAlternateColorCodes('&', line));
        }
        if (this.lore == null) {
            this.lore = Lists.newArrayList();
        }
        this.lore.addAll(modifiedLore);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        addLore(lore.toArray(new String[lore.size()]));
        return this;
    }

    public ItemBuilder resetLore() {
        initItemMeta();
        this.itemMeta.setLore(Lists.newArrayList());
        return this;
    }

    public ItemBuilder setDamage(short damage) {
        setDurability(damage);
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        initItemMeta();
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        initItemMeta();
        this.itemMeta.setUnbreakable(unbreakable);
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder setMaterialData(MaterialData materialData) {
        this.setData(materialData);
        return this;
    }

    public ItemBuilder hideEnchants() {
        this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemStack build() {
        if (getType() == Material.AIR) {
            return this;
        }
        if (this.itemMeta != null) {
            this.itemMeta.setLore(this.lore);
            setItemMeta(this.itemMeta);
        }

        return this;
    }
}
