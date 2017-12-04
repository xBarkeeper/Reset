package de.rissi.Reset.Reset;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Reset extends JavaPlugin implements CommandExecutor, Listener
{

	ConsoleCommandSender	console		= Bukkit.getServer().getConsoleSender();
	String					worldName	= "playingWorld";

	@Override
	public void onEnable() {
		super.onEnable();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents((Listener) this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (Bukkit.getWorld(worldName) == null)
		{
			createNewWorld(worldName);
		}
		if (e.getPlayer().getWorld().getName() != worldName)
		{
			resetPlayer(e.getPlayer());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if (p.getHealth() - e.getDamage() <= 0)
			{
				e.setCancelled(true);
				ArrayList<Player> arrayOfPlayer = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				for (Player player : arrayOfPlayer)
				{
					player.teleport(Bukkit.getWorld("world").getSpawnLocation());
					player.kickPlayer(ChatColor.RED + p.getDisplayName() + " ist gestorben" + ChatColor.GREEN
							+ "\n Die Welt wir Resetet...");
				}
				Bukkit.unloadWorld(Bukkit.getWorld(worldName), false);
				delete(new File(worldName));
				createNewWorld(worldName);
			}
		}
	}
	
	private boolean resetPlayer(Player player)
	{
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
		return true;
	}

	private boolean delete(File file) {
		if (file.isDirectory())
		{
			for (File currentFile : file.listFiles())
			{
				if (!delete(currentFile))
				{
					return false;
				}
			}
		}

		if (!file.delete())
		{
			return false;
		}
		return true;
	}

	private String createNewWorld(String name) {
		String returnMessage;
		try
		{
			WorldCreator wc = new WorldCreator(worldName);
			wc.environment(Environment.NORMAL);
			wc.generateStructures(true);
			returnMessage = ChatColor.BLUE + "Welt wurde erstellt: " + ChatColor.GREEN + wc.createWorld();
		} catch (Exception ex)
		{
			returnMessage = ChatColor.RED + "Fehler beim erstellen der Welt: " + ChatColor.GREEN + ex.getMessage();
		}
		return returnMessage;
	}
}
