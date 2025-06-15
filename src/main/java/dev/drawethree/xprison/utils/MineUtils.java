package dev.drawethree.xprison.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import joserodpt.realmines.api.RealMinesAPI;
import joserodpt.realmines.api.event.MineBlockBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MineUtils {

    private static final RealMinesAPI minesAPI = RealMinesAPI.getInstance();
    private static final List<String> mines = new ArrayList<>();
    // Region Name - RealMines mine
    private static final HashMap<String, String> exceptMines = new HashMap<>();

    static {
        exceptMines.putIfAbsent("pvpmina", "Minapvp");
    }

    private static boolean isEnable() {
        var pluginName = "RealMines";
        var minePlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return minePlugin != null && minePlugin.isEnabled();
    }

    public static void addBlocks(Player player, List<Block> list) {
        if (!isEnable()) return;
        if (list.isEmpty()) return;
        var firsBlock =  list.get(0);
        var rawRegion = getRawRegion(firsBlock);
        if (rawRegion != null) {
            mineAddBlock(player, list, exceptMines.get(rawRegion));
        } else {
            var mineRegion = getRegion(firsBlock);
            if (mineRegion == null) return;
            mineRegion = mineRegion.replaceAll("\\D", "");
            var romanNumber = toRoman(Integer.parseInt(mineRegion));
            mineAddBlock(player, list, romanNumber);
        }
    }

    private static void mineAddBlock(Player player, List<Block> list, String mineName) {
        var selMine = minesAPI.getMineManager().getMine(mineName);
        if (selMine == null) return;
        for (Block block : list) {
            var mineBreakEvent = new MineBlockBreakEvent(player, selMine, block, true);
            selMine.processBlockBreakEvent(mineBreakEvent, false);
            if (selMine.getMinedBlocks() + list.size() >= 42000) {
                selMine.reset();
                break;
            }
        }
    }

    private static String toRoman(int number) {
        if (number >= 1 && number <= 100) {
            String[] units = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
            String[] tens = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
            String[] hundreds = new String[]{"", "C"};

            return hundreds[number / 100] + tens[(number % 100) / 10] + units[number % 10];
        } else {
            return number+"";
        }
    }

    @Nullable
    private static String getRegion(Block block) {
        var set = getRegions(block);
        for (var region : set.getRegions()) {
            if (region.getId().startsWith("mine-") || region.getId().startsWith("mina")) {
                try {
                    Integer.parseInt(region.getId().substring(4));
                    return region.getId();
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;
    }

    @Nullable
    private static String getRawRegion(Block block) {
        var set = getRegions(block);
        for (var region : set.getRegions()) {
            if (exceptMines.containsKey(region.getId())) return region.getId();
        }
        return null;
    }

    private static ApplicableRegionSet getRegions(Block block) {
        Location loc = BukkitAdapter.adapt(block.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(loc);

    }

    private static boolean checkXPrison(ApplicableRegionSet region) {
        if (Bukkit.getPluginManager().getPlugin("X-Prison") == null) return false;
        Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get("upc-enchants");
        if (flag == null) return false;
        return region.testState(null, (StateFlag) flag);
    }

    public static List<String> getMines() {
        return mines;
    }
}