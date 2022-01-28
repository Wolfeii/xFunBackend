package se.xfunserver.xfunbackend.spigot.gui.object;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import se.xfunserver.xfunbackend.spigot.user.object.xFunUser;
import se.xfunserver.xfunbackend.spigot.utilities.ItemBuilder;

import java.util.List;

public abstract class InteractItem extends ItemBuilder {

    @Getter
    private static List<InteractItem> interactItems = Lists.newArrayList();

    public InteractItem(Material material){
        super(material);

        interactItems.add(this);
    }

    public abstract void onClick(xFunUser synergyUser, Action action);

}