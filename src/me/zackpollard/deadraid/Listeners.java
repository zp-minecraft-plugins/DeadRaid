package me.zackpollard.deadraid;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {
	
	private DeadRaid plugin;
	
	public Listeners(DeadRaid plugin){
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event){
		
		if(event.getEntity() instanceof Player){
			
			Player player = (Player) event.getEntity();
			
			if(plugin.raiders.containsKey(player)){
				
				if(player.getHealth() - event.getDamage() <= 0){
					
					event.setCancelled(true);
					
					plugin.playerManager.respawnPlayer(player);
					
					player.sendMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "You died! You have died a total of " + plugin.deaths.get(player) + " times!");
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		
		Player player = event.getPlayer();
		
		if(plugin.raiders.containsKey(player)){
			
			if(!plugin.raiders.get(player).booleanValue()){
				
				event.setCancelled(true);
				player.sendMessage(ChatColor.GRAY +"[DeadRaid] - " + ChatColor.ITALIC + "You can't teleport while in a deadraid.");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(plugin.raiders.containsKey(player)){
			
			plugin.playerManager.quitRaid(player);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		if(plugin.raiders.containsKey(player)){
			
			plugin.playerManager.quitRaid(player);
		}
	}
}
