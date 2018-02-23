package me.zackpollard.deadraid;

import java.util.Random;

import me.zackpollard.deadraid.DeadRaid.RaidState;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class PlayerManager {
	
	private DeadRaid plugin;
	
	public PlayerManager(DeadRaid plugin){
		this.plugin = plugin;	
	}
	
	public void tpStart(Player player){
					
		plugin.oldLocation.put(player, player.getLocation());
		plugin.inventories.put(player, player.getInventory().getContents());
		player.teleport(plugin.startPoint);
			
		this.giveStartItems(player);
	}
	
	public boolean addPlayer(Player player){
		
		if(!plugin.raiders.containsKey(player)){
			
			this.tpStart(player);
			
			plugin.deaths.put(player, 0);
			plugin.raiders.put(player, false);
			
			return true;
		}
		return false;
	}
	
	public void giveStartItems(Player player){
		
		player.getInventory().clear();
		
		ItemStack ironAxe = new ItemStack(Material.IRON_AXE, 1);
		ironAxe.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		player.getInventory().addItem(ironAxe);
		player.getInventory().addItem(new ItemStack(Material.BREAD, 3));
	}
	
	public void giveRewardItem(Player player){
		
		Random random = new Random(System.currentTimeMillis());
		
		ItemStack prize = new ItemStack(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) prize.getItemMeta();
		Enchantment enchant = Enchantment.values()[random.nextInt(Enchantment.values().length)];
		meta.addStoredEnchant(enchant, enchant.getMaxLevel(), true);
		prize.setItemMeta(meta);
		
		player.getInventory().addItem(prize);						
		player.sendMessage(ChatColor.GREEN + "Your prize is in your inventory. Congratulations!");
	}
	
	public void respawnPlayer(Player player){
		
		for(ItemStack i : player.getInventory().getArmorContents()){
			if(i != null && i.getType() != Material.AIR) {
				player.getWorld().dropItemNaturally(player.getLocation(), i);
			}
		}
		for(ItemStack i : player.getInventory().getContents()){
			if(i != null && i.getType() != Material.AIR) {
				player.getWorld().dropItemNaturally(player.getLocation(), i);
			}
		}
		
		player.getInventory().clear();
		
		plugin.deaths.put(player, plugin.deaths.get(player) +1);
		plugin.raiders.put(player, true);
		player.teleport(plugin.startPoint);
		plugin.raiders.put(player, false);
		
		player.getInventory().addItem(new ItemStack(Material.IRON_AXE, 1));
		player.setHealth(player.getMaxHealth());
	}
	
	public void startRaid(){
		
		Location loc = plugin.startPoint;
		loc.getWorld().setTime(13000);
		loc.getWorld().strikeLightning(loc.add(0,20,0));
		plugin.dawnWatch = plugin.getServer().getScheduler().runTaskTimer(plugin, new CheckDawn(plugin), 20, 20);
		plugin.state = RaidState.ACTIVE;
	}
	
	public void resetRaid(){
		
		plugin.deaths.clear();
		plugin.inventories.clear();
		plugin.oldLocation.clear();
		plugin.raiders.clear();
		plugin.startPoint = null;
	}

	public boolean quitRaid(Player player){
		
		if(plugin.raiders.containsKey(player)){
			
			plugin.raiders.remove(player);
			plugin.deaths.remove(player);
			
			player.getInventory().clear();
			player.teleport(plugin.oldLocation.get(player));
			if(plugin.inventories.get(player).length != 0){
				for(ItemStack item : plugin.inventories.get(player)){
					if(item != null){
						player.getInventory().addItem(item);
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
}