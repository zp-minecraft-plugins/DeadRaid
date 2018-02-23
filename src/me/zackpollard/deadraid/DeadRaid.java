package me.zackpollard.deadraid;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class DeadRaid extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	//Maps the players to the number of times they have died
	HashMap<Player, Integer> deaths = new HashMap<Player, Integer>();
	//Maps the location of the player when they are teleported to the raid
	HashMap<Player, Location> oldLocation = new HashMap<Player, Location>();
	//Stores the players inventories for when they are teleported incase they accidentally keep items
	HashMap<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
	//Player player, Boolean dead?
	HashMap<Player, Boolean> raiders = new HashMap<Player, Boolean>();
	RaidState state;
	Location startPoint;
	BukkitTask dawnWatch;
	
	PlayerManager playerManager = new PlayerManager(this);
	
	enum RaidState {
		INACTIVE, WAITING, ACTIVE
	}
	
	public void onEnable() {
		
		state = RaidState.INACTIVE;
		new Listeners(this);
		this.getCommand("deadraid").setExecutor(new RaidCommand(this));
		log.info("DeadRaid enabled!");
	}
	
	public void onDisable() {
		
		log.info("DeadRaid disabled!");
	} 
}
