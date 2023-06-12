package de.arrel.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Eventlistner implements Listener {

	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws InterruptedException, IOException {
		Player e = event.getPlayer();
		File file = new File("plugins/userdata/" + "playermoneydata.yml");
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(file);
		
		if(playerdata.getBoolean(e.getName() + " isCreated") == true) { //prüfen ob der Spieler schon Angelegt wurde
			
		}else {															//sonst den Spieler neu erstellen
			playerdata.set(e.getName() + " isCreated", true);
			playerdata.set(e.getName() + " Money", 1000);
			playerdata.save(file);
		}
	}
}
