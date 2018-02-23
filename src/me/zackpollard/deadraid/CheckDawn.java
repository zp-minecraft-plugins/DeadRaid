package me.zackpollard.deadraid;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.zackpollard.deadraid.DeadRaid.RaidState;

public class CheckDawn implements Runnable {

	private DeadRaid plugin;
	
	public CheckDawn(DeadRaid plugin){
		this.plugin = plugin;
	}
	
	public void run(){
		
		if(plugin.state == RaidState.ACTIVE){
			
			World world = plugin.startPoint.getWorld();
			
			if(world.getTime() < 13000 || world.getTime() > 23000){
				
				plugin.state = RaidState.INACTIVE;
				
				ArrayList<Player> survivors = new ArrayList<Player>();
				
				for(Player p : plugin.deaths.keySet()){
					
					int deaths = plugin.deaths.get(p);
					if(deaths == 0) survivors.add(p);
					
					p.sendMessage(ChatColor.BLUE + "Dawn has arrived, and " + (deaths > 0? String.format("you died %s time%s.", deaths, deaths==1 ? "" : "s"): "you survived the whole night!"));
					
					plugin.playerManager.quitRaid(p);
				}
				
				int numSurvivors = survivors.size();
				if(numSurvivors > 0){
					
					Random random = new Random(System.currentTimeMillis());
					Player winner;
					if(numSurvivors == 1){
						winner = survivors.get(0);
						Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[DeadRaid] - " + ChatColor.ITALIC + "As the only survivor, " + winner.getName() + " won the deadraid!");
					} else {
						winner = survivors.get(random.nextInt(numSurvivors));
						Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[DeadRaid] - " + "Of the " + numSurvivors + " survivors, " + winner.getName() + " was randomly selected as the winner of the deadraid!");
					}
					
					plugin.playerManager.giveRewardItem(winner);
				} else {
					Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[DeadRaid] - " + "There were no survivors this deadraid.");
				}
				plugin.dawnWatch.cancel();
			}
		}
	}
}
