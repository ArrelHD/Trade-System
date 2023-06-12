package de.arrel.groupmoney.system;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.arrel.main.Main;
import de.arrel.main.SaveMain;

public class StadtCMD implements CommandExecutor{
	private static String stadtPath = "plugins/Money/stadt.yml";
	private String userMoneyPath = "plugins/userdata/playermoneydata.yml";
	
	private Main main;
	private SaveMain save;
	
	public StadtCMD(Main main) {
		this.main = main;
		this.save = this.main.getSave();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		//  /Stadt info
		
		if(save == null) {
			return true;
		}
		
		if(save.getStringFile(stadtPath, "created") == null) {
			save.log("<br>StadtKonto wird erstellt.");
			
			save.saveFile(stadtPath, "created", true);
			save.saveFile(stadtPath, "money", 5000);
			save.saveFile(stadtPath, "owner", "");
		}

		if(args.length >= 1) {
			
			switch (args[0]) {
			case "info":
				if(!isAdmin(sender)) {
					sender.sendMessage("§aDu kannst das nicht tun.");
					break;
				}
				sender.sendMessage("§aFolgende Stadt-Konto Infos gibt es:");
				sender.sendMessage("§aVerwalter: " + save.getStringFile(stadtPath, "owner"));
				sender.sendMessage("§aGulden: " + save.getStringFile(stadtPath, "money"));
				sender.sendMessage("");
				
				break;
			
			case "pay":
				if(args.length == 2) {
					try {
						Integer.parseInt(args[1]);
					}catch (NumberFormatException e) {
						sender.sendMessage("§cDas ist keine Zahl");
						return true;
					}
					if(Integer.parseInt(args[1]) <= 0) {
						sender.sendMessage("§cDie Zahl muss größer 0 sein!");
						return true;
					}
					
					int currentMoney = Integer.parseInt(save.getStringFile(stadtPath, "money"));
					int sendMoney = Integer.parseInt(args[1]);
					int senderMoney = 999999999;
					if(sender instanceof Player) {
						senderMoney = Integer.parseInt(save.getStringFile(userMoneyPath, sender.getName() + " Money"));
					}
					if(senderMoney < sendMoney) {
						sender.sendMessage("§cDu hast nicht genug Geld.");
						return true;
					}
					
					senderMoney -= sendMoney;
					currentMoney += sendMoney;
					save.saveFile(stadtPath, "money", currentMoney);
					if(sender instanceof Player) {
						save.saveFile(userMoneyPath, sender.getName() + " Money", senderMoney);
					}
					sender.sendMessage("§aDu hast erfolgreich §6" + sendMoney + " Gulden §aauf das Stadtkonto gezahlt");
					save.log("Der Spieler " + sender.getName() + " hat " + sendMoney + " Gulden auf das Stadtkonto gezahlt. <br>");
				}else {
					sender.sendMessage("§aBitte benutze /stadt pay [Zahl]");
				}
				break;
			case "transfer":
				if(!isAdmin(sender)) {
					sender.sendMessage("§aDu kannst das nicht tun.");
					break;
				}
				if(args.length == 3) {
					
					Player t = null;
					for(Player all : Bukkit.getOnlinePlayers()) {
						if(args[1].equalsIgnoreCase(all.getName())) {
							t = all;
							break;
						}
					}
					if(t == null) {
						sender.sendMessage("§aDer Spieler ist nicht online.");
						break;
					}
					
					int transferMoney;
					
					try {
						transferMoney = Integer.parseInt(args[2]);
					}catch (NumberFormatException e) {
						sender.sendMessage("§cDas ist keine Zahl.");
						break;
					}
					if(transferMoney <= 0) {
						sender.sendMessage("§cDer Betrag muss größer als 0 sein!");
						break;
					}
					
					int stadtMoney = Integer.parseInt(save.getStringFile(stadtPath, "money"));
					int targetMoney = Integer.parseInt(save.getStringFile(userMoneyPath, t.getName() + " Money"));
					
					stadtMoney -= transferMoney;
					targetMoney += transferMoney;
					
					save.saveFile(stadtPath, "money", stadtMoney);
					save.saveFile(userMoneyPath, t.getName() + " Money", targetMoney);
					
					sender.sendMessage("§aDu hast dem Spieler " + t.getName() + " erfolgreich §6" + transferMoney + " Gulden §agezahlt.");
					save.log(sender.getName() + " hat dem Spieler " + t.getName() + " " + transferMoney + " Gulden aus der Stadtkasse gezahlt. <br>");
					t.sendMessage("§aDir wurde von der Stadt §6" + transferMoney + " Gulden §aüberwiesen.");

				}else {
					sender.sendMessage("§aBitte benutze /stadt transfer [Spielername] [Zahl]");
				}
				break;
			case "admin":
				//Das Adminmenu. 
				if(!sender.isOp()) {
					sender.sendMessage("§aDas Darfst du nicht.");
					return true;
				}
				if(args.length < 2) {
					sender.sendMessage("§cBitte mach ma /stadt admin setowner");
					break;
				}
				switch (args[1]) {
				case "setowner":
					if(args.length != 3) {
						sender.sendMessage("§cBrudi /stadt admin setowner [Name] ");
						sender.sendMessage("§cNicht mehr und net weniger");
						break;
					}
					save.saveFile(stadtPath, "owner", args[2]);
					sender.sendMessage("§aDu hast erfolgreich den Owner auf: " + args[2] + " gesetzt");
					break;

				default:
					break;
				}
				
				
				break;
			default:
				break;
			}
			
		}else {
			//Hilfeseite anzeigen
		}
		
		return true;
	}
	
	public boolean isAdmin(CommandSender sender) {
		if(sender.isOp()) {
			return true;
		}
		
		if(save.getStringFile(stadtPath, "owner").equalsIgnoreCase(sender.getName())) {
			return true;
		}
		
		return false;
	}

	public String getUserMoneyPath() {
		return userMoneyPath;
	}

	public static String getStadtPath() {
		return stadtPath;
	}

}
