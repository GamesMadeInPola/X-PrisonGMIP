package dev.drawethree.xprison.utils;

import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import at.pcgamingfreaks.Minepacks.Bukkit.Minepacks;
import dev.drawethree.xprison.utils.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public class BackpackUtils {

    public static void addBlocks(Player player, ItemStack item) {
        var bpApi = getMinepacks();
        if (bpApi == null) return;
        var pbp = bpApi.getBackpackCachedOnly(player);
        if (pbp == null) return;
        if (InventoryUtils.hasSpace(pbp.getInventory())) {
            pbp.addItem(item);
        }
    }

    public static boolean isBackpackFull(Player player) {
        var bpApi = getMinepacks();
        if (bpApi == null) return false;
        var pbp = bpApi.getBackpackCachedOnly(player);
        if (pbp == null) return false;
        return !InventoryUtils.hasSpace(pbp.getInventory());

    }

    public static Inventory getInv(Player player) {
        var bpApi = getMinepacks();
        if (bpApi == null) return null;
        var pbp = bpApi.getBackpackCachedOnly(player);
        if (pbp == null) return null;
        return pbp.getInventory();
    }

    public static MinepacksPlugin getMinepacks() {
        Plugin bukkitPlugin = Bukkit.getPluginManager().getPlugin("Minepacks");
        if(!(bukkitPlugin instanceof MinepacksPlugin)) {
            return null;
        }
        return (MinepacksPlugin) bukkitPlugin;
    }

}
