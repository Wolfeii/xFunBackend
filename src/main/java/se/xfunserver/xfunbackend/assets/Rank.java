package se.xfunserver.xfunbackend.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import se.xfunserver.xfunbackend.spigot.Core;

import java.util.Arrays;
import java.util.Iterator;

@RequiredArgsConstructor
public enum Rank {

    NONE(0, "NONE", ChatColor.GRAY, "rank.none"),
    GOLD(1, "GOLD", ChatColor.GOLD, "rank.gold"),
    DIAMOND(2, "DIAMOND", ChatColor.AQUA, "rank.diamond"),
    RUBY(3, "RUBY", ChatColor.RED, "rank.ruby"),
    PLATINUM(4, "PLATINUM", ChatColor.DARK_BLUE, "rank.platinum"),
    CREATOR(5, "CREATOR", ChatColor.GOLD, "rank.creator"),

    JRMODERATOR(10, "JRMODERATOR", ChatColor.AQUA, "rank.jrmoderator"),
    MODERATOR(11, "MODERATOR", ChatColor.DARK_AQUA, "rank.moderator"),
    SRMODERATOR(12, "JRMODERATOR", ChatColor.BLUE, "rank.srmoderator"),
    DEVELOPER(13, "DEV", ChatColor.DARK_PURPLE, "rank.developer"),
    ADMIN(14, "ADMIN", ChatColor.RED, "rank.admin");

    @Getter
    private final Integer id;
    @Getter
    private final String codeName;
    @Getter
    private final ChatColor color;
    private final String node;

    public String getPermission() {
        return Core.getPlugin().getManifest().permissionPrefix() + "." + node;
    }

    public static Rank fromName(String codeName) {
        for (Rank rank : values()) {
            if (rank.getCodeName().equalsIgnoreCase(codeName)) {
                return rank;
            }
        }
        return null;
    }

    public boolean isHigherThan(Rank rank){
        return getId() > rank.getId();
    }

    public boolean isHigherThanAndEqualTo(Rank rank){
        return getId() >= rank.getId();
    }

    public boolean isLowerThan(Rank rank){
        return getId() < rank.getId();
    }

    public boolean isLowerThanAndEqualTo(Rank rank){
        return getId() <= rank.getId();
    }

    public boolean isStaff() {
        return this.isHigherThanAndEqualTo(Rank.JRMODERATOR);
    }

    public boolean isUserRank() {
        return true;
    }

    public static Rank getLowestDonatorRank() {
        return GOLD;
    }

    public static Rank getHighestDonatorRank() {
        return PLATINUM;
    }

    public static Rank getLowestStaffRank() {
        return JRMODERATOR;
    }

    public Rank getHighestStaffRank() {
        return ADMIN;
    }

    public static Iterator<Rank> getStaffRanks() {
        Rank[] staffRanks = new Rank[] {
                JRMODERATOR, MODERATOR, SRMODERATOR, DEVELOPER, ADMIN
        };
        return Arrays.stream(staffRanks).iterator();
    }

    public static Iterator<Rank> getDonatorRanks() {
        Rank[] donatorRanks = new Rank[] {
                GOLD, DIAMOND, RUBY, PLATINUM
        };
        return Arrays.stream(donatorRanks).iterator();
    }

    public static Rank getRankBasedOnPermission(Player player) {
        return Arrays.stream(values())
                .filter(rank -> player.hasPermission(rank.getPermission()))
                .reduce((a, b) -> b)
                .orElse(NONE);
    }




}
