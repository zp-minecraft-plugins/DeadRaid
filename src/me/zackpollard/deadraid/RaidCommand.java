package me.zackpollard.deadraid;

import me.zackpollard.deadraid.DeadRaid.RaidState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaidCommand implements CommandExecutor {
	
	DeadRaid plugin;
	
	public RaidCommand(DeadRaid plugin){
		
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player){
			
			Player player = (Player) sender;
			
			if(args.length == 1){
				
				if(args[0].equalsIgnoreCase("open")){
					
					if(player.hasPermission("deadraid.host")){
						
						switch(plugin.state){
						case ACTIVE:
							player.sendMessage(ChatColor.RED + "There is already an active deadraid.");
							break;
						case WAITING:
							player.sendMessage(ChatColor.RED + "There is already a deadraid waiting to start.");
						case INACTIVE:
							plugin.state = RaidState.WAITING;
							plugin.playerManager.resetRaid();
							plugin.startPoint = player.getLocation();
							for(Player p : Bukkit.getOnlinePlayers()){
								
								if(player.hasPermission("deadraid.join")){
									
									p.sendMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "A deadraid is now open to entrants.");
								}
							}
							break;
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("begin")){
					
					if(player.hasPermission("deadraid.host")){
						
						switch(plugin.state){
						case ACTIVE:
							player.sendMessage(ChatColor.RED + "There is already an active deadraid.");
							break;
						case INACTIVE:
							player.sendMessage(ChatColor.RED + "There is no pending deadraid.");
							break;
						case WAITING:
							if(!plugin.raiders.isEmpty()){
								
								plugin.playerManager.startRaid();
								
								for(Player p : Bukkit.getOnlinePlayers()){
									
									if(p.hasPermission("deadraid.join")){
										
										p.sendMessage(ChatColor.DARK_GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "The deadraid has begun!");
									}
								}
								return true;
								
							} else {	
								player.sendMessage(ChatColor.RED + "There are no participants");
								return true;
							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("cancel")){
					
					if(player.hasPermission("deadraid.host")){
						
						switch(plugin.state){
						case INACTIVE:
							
							player.sendMessage(ChatColor.RED + "There is no deadraid to cancel.");
							break;
						case WAITING:
						case ACTIVE:
							plugin.state = RaidState.INACTIVE;
							
							for(Player p : plugin.raiders.keySet()){
								
								plugin.playerManager.quitRaid(p);
							}
							
							if(plugin.dawnWatch != null) plugin.dawnWatch.cancel();
							
							for(Player p : Bukkit.getOnlinePlayers()){
								
								if(p.hasPermission("deadraid.join")){
									
									p.sendMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "The deadraid has been cancelled.");
								}
							}
							break;
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to do this!");
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("join")){
					
					if(player.hasPermission("deadraid.join")){
						
						switch(plugin.state){
						case ACTIVE:
							player.sendMessage(ChatColor.RED + "Too late! The deadraid has already begun!");
							break;
						case INACTIVE:
							player.sendMessage(ChatColor.RED + "There is currently no deadraid to join.");
							break;
						case WAITING:
							if(plugin.playerManager.addPlayer(player)){
								
								player.sendMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "You have joined the pending deadraid. If you logout you will leave it.");
							} else {
								
								player.sendMessage(ChatColor.RED + "You have already joined the deadraid!");
							}
							break;
						}
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
				}
				
				if(args[0].equalsIgnoreCase("leave")){
					
					if(player.hasPermission("deadraid.join")){
					
						switch(plugin.state){
						case INACTIVE:
							player.sendMessage(ChatColor.RED + "There is no deadraid to leave.");
							break;
						case ACTIVE:
						case WAITING:
							
							if(plugin.playerManager.quitRaid(player)){
								
								player.sendMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "You left the deadraid.");
							} else {
								
								player.sendMessage(ChatColor.RED + "You are not in the deadraid.");
							}
							break;
						}
						
						return true;
					} else {
						player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return true;
					}
				}
			}
			player.sendMessage(ChatColor.BLUE + "These are the commands for Deadraid");
			player.sendMessage(ChatColor.DARK_GREEN + "/deadraid join to join a deadraid");
			player.sendMessage(ChatColor.DARK_GREEN + "/deadraid leave to leave a deadraid");
			player.sendMessage(ChatColor.DARK_GREEN + "/deadraid open to open a deadraid to entrants (admin only)");
			player.sendMessage(ChatColor.DARK_GREEN + "/deadraid begin to start a deadraid (admin only)");
			player.sendMessage(ChatColor.DARK_GREEN + "/deadraid cancel to end a deadraid (admin only)");
			return true;
		}
		
		sender.sendMessage("You must be a Player to interact with DeadRaid!");
		return true;
	}
}