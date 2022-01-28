package se.xfunserver.xfunbackend.spigot.user.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import se.xfunserver.xfunbackend.assets.Rank;
import se.xfunserver.xfunbackend.assets.xFunLogger;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;
import se.xfunserver.xfunbackend.spigot.botsam.object.SamMessage;
import se.xfunserver.xfunbackend.spigot.user.event.UserLoadEvent;
import se.xfunserver.xfunbackend.spigot.utilities.UtilSound;
import se.xfunserver.xfunbackend.spigot.utilities.UtilString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class xFunUser {

    private final UUID uuid;
    private final String name;
    private Rank rank;
    private boolean loaded;
    private Timestamp joinedOn;
    @Setter
    private UserExperience userExperience;
    @Getter @Setter
    private String messageCache = "";
    private UserLoadEvent.UserLoadType loadType = UserLoadEvent.UserLoadType.RETRIEVED_FROM_DATABASE;
    @Setter
    private boolean ableToLoad;

    public xFunUser(UUID uuid, String name, Rank rank, UserLoadEvent.UserLoadType loadType, boolean save) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.joinedOn = new Timestamp(System.currentTimeMillis());
        this.userExperience = UserExperience.NOOB;
        this.loadType = loadType;

        if (save) {
            Core.getPlugin().getUserManager().getUsers().put(uuid, this);
        }
    }

    public void setJoinedOn(Timestamp joinedOn) {
        if (joinedOn == null) {
            this.joinedOn  = new Timestamp(System.currentTimeMillis());
            return;
        }
        this.joinedOn = joinedOn;
    }

    public xFunUser(UUID uuid, String name, Rank rank, UserLoadEvent.UserLoadType loadType) {
        this(uuid, name, rank, loadType, true);
    }

    public xFunUser(UUID uuid, String name, Rank rank) {
        this(uuid, name, rank, UserLoadEvent.UserLoadType.NEW_INSTANCE, true);
    }

    public void updateDatabase(boolean quitEvent) {
        xFunLogger.info("Tittar ifall om användaren är online...");

        if (!quitEvent && this.isOnline()) {
            xFunLogger.info(getName() + " är just nu online, fortsätter inte.");
            return;
        }

        xFunLogger.info("Spelaren är just nu offline.");
        Core.getPlugin().getUserManager().updateUserTable(this);
        Core.getPlugin().getUserManager().getUsers().remove(this.uuid);
        Core.getPlugin().getEconomyManager().updateEconomyForUser(this);
    }

    public void updateDatabase() {
        updateDatabase(false);
    }

    public void attemptLoading() {
        if (getPlayer() != null) {
            UserLoadEvent loadEvent = new UserLoadEvent(this, this.loadType);
            Core.getPlugin().getServer().getPluginManager().callEvent(
                    loadEvent
            );

            this.rank = Rank.getRankBasedOnPermission(getPlayer());
        }
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline();
    }

    public boolean hasRank(Rank rank) {
        return this.rank == rank;
    }

    public void delete() {
        if (getPlayer() != null && getPlayer().isOnline()) {
            return;
        }

        Core.getPlugin().getUserManager().getUsers().remove(getUuid());
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayer(getUuid());
    }

    public void message(String... messages) {
        Arrays.stream(messages)
                .forEach(s ->
                        getPlayer()
                                .sendMessage(
                                        C.translateColors(s)));
    }

    public void clean() {
        Player player = getPlayer();

        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.closeInventory();
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setExp(0);
        player.setFlySpeed(1);
        player.setFireTicks(0);

        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public void heal(){
        Player player = getPlayer();

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
    }

    public void teleport(xFunUser target){
        //TODO: Add a fix so they can't teleport out of combat and add several checks for combat tag e.t.c.
        // And make a function with a countdown..
        getPlayer().teleport(target.getPlayer());
    }

    public void info(String... messages) {
        Sam.getRobot().info(getPlayer(), messages);
    }

    public void announcement(String... messages) {
        Sam.getRobot().announcement(getPlayer(), messages);
    }

    public void sam(SamMessage samMessage) {
        Sam.getRobot().sam(getPlayer());
    }

    public void error(String... messages) {
        Sam.getRobot().warning(getPlayer(), messages);
    }

    public void warning(String... messages) {
        Sam.getRobot().warning(getPlayer(), messages);
    }

    public void sendModificationMessage(MessageModification modification, String... messages) {
        switch (modification) {
            case CENTERED:
                xFunLogger.debug("Skickar ett CENTRERAT meddelande till " + getName().toUpperCase());

                Arrays.stream(messages).forEach(s ->
                        getPlayer().sendMessage(
                                UtilString.centered(s)));
                break;
            default:
                Arrays.stream(messages).forEach(s -> getPlayer()
                        .sendMessage(C.translateColors(s)));
                break;
        }
    }

    public void sendModificationMessage(MessageModification modification, List<String> messages) {
        switch (modification) {
            case CENTERED:
                xFunLogger.debug("Skickar ett CENTRERAT meddelande till " + getName().toUpperCase());

                messages.forEach(s -> getPlayer().sendMessage(UtilString.centered(s)));
                break;
            default:
                messages.forEach(s -> getPlayer().sendMessage(s));
                break;
        }
    }

    public UtilSound getSoundControl() {
        return new UtilSound(this);
    }

    public boolean hasPermission(String node){
        return this.hasPermission(node, true);
    }

    public boolean hasPermission(String node, boolean message) {
        String permission = Core.getPlugin().getManifest().permissionPrefix() + "." + node;
        if (getPlayer().hasPermission(permission)) {
            return true;
        } else {
            if (message) {
                this.sendNoPermission();
            }
            return false;
        }
    }

    public void sendNoPermission() {
        error(SamMessage.NO_PERMISSIONS.getRandom());
    }

    public void kick(String reason){
        getPlayer().kickPlayer(reason);
    }

}
