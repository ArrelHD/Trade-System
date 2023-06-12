package de.arrel.chest.system.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.arrel.main.Main;
import de.arrel.main.SaveMain;



public class AddAllowedShopOwners implements CommandExecutor{
	Main main;
	SaveMain save;
	
	String path = "plugins/Money/allowedShopOwners.yml";
	
	public AddAllowedShopOwners(Main main) {
		this.main = main;
		this.save = main.getSave();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		
		if(sender.isOp()) {
			if(args.length != 1) {
				sender.sendMessage("Bitte /mobadd [Name]");
				return true;
			}
			if(save.getStringFile(path, "players") != null && !save.getStringFile(path, "players").equalsIgnoreCase("")) {
				save.saveFile(path, "players", save.getStringFile(path, "players") + "," + args[0]);
				sender.sendMessage("Der Spieler:" + args[0] + " wurde hinzugefügt");
			}else {
				save.saveFile(path, "players", args[0]);
				sender.sendMessage("Der Spieler:" + args[0] + " wurde hinzugefügt");
			}
		}else {
			sender.sendMessage("Darfste Net.");
			return true;
		}
		
		return true;
	}
	
	public boolean canOwnShop(String name) {
		String all[] = save.getStringFile(path, "players").split(",");
		
		for(int i = 0; i < all.length; i++) {
			if(all[i].equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		return false;
	}
	
}
