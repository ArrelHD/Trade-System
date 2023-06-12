package de.arrel.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Cmdgold implements CommandExecutor {
	String prefix = "§r[§6Money§r]§a ";
	
	Main main;
	SaveMain save;
	
	public Cmdgold(Main main) {
		this.main = main;
		this.save = main.getSave();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		File file = new File("plugins/userdata/" + "playermoneydata.yml");
		YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(file);

		if(cmd.getName().equalsIgnoreCase("money")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {

					p.sendMessage(prefix + "§a Hilfe:");
					p.sendMessage(prefix + "§a /money (Spieler) - Zeigt dein Geld an -Opt. Geld von anderen Spielern");
					p.sendMessage(prefix + "§a /pay [Name] [Betrag] Damit zahlst du Geld an Spieler");
					p.sendMessage(prefix + "§a /money help - Zeigt die Hilfeseite an");
					
					return true;
				}else if(p.isOp()){
					Player t = p.getPlayer();
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						if(e.getPlayer().getName().equalsIgnoreCase(args[0])) {
							t = Bukkit.getServer().getPlayer(args[0]);
						}
					}
					p.sendMessage(prefix + "§aDer Spieler §c" + t.getName() + " §ahat derzeit §c" + playerdata.getLong(t.getName() + " Money") + " §6Gulden");
				}
			}else{
				p.sendMessage(prefix + "§aDu hast derzeit §c" + playerdata.getLong(p.getName() + " Money") + " §6Gulden!");
			}
			
		}
		
		if(cmd.getName().equalsIgnoreCase("pay")) {
			if(args.length == 2) {
				
				Long inputmoney = (long) 0;
				Long payermoney = playerdata.getLong(p.getName()+ " Money");
				Long receivermoney = (long) 0;
				
				//if(!dd[1].toString().matches("1") && !args[1].toString().matches("2") && !args[1].toString().matches("3")&& !args[1].toString().matches("4") && !args[1].toString().matches("5")&& !args[1].toString().matches("6") && !args[1].toString().matches("7")&& !args[1].toString().matches("8") && !args[1].toString().matches("9")&& !args[1].toString().matches("0")) {
					
				//}else {
				try {
					inputmoney = (long) Integer.parseInt(args[1]);
				}catch (NumberFormatException e) {
					p.sendMessage("§cDu musst eine Zahl angeben");
					return true;
				}
				//}
				
				if(args[0] != null){
					
					Player t = p.getPlayer();
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						if(e.getPlayer().getName().equalsIgnoreCase(args[0])) {
							t = Bukkit.getServer().getPlayer(args[0]);
						}
					}
					receivermoney = playerdata.getLong(t.getName()+ " Money");
					if(Integer.parseInt(args[1]) >= 0) {
						if(playerdata.getLong(p.getName() + " Money") >= inputmoney) {
							if(!p.getName().equalsIgnoreCase(t.getName())) {
								p.sendMessage(prefix + "§aDu hast §c" + inputmoney + " §6Gulden §aerfolgreich §c" + t.getName() + " §agegeben");
								t.sendMessage(prefix + "§c" + p.getName() + " §ahat dir erfolgreich §c" + inputmoney + " §6Gulden§a gegeben");
								receivermoney = receivermoney+inputmoney;
								payermoney = payermoney-inputmoney;
								playerdata.set(p.getName() + " Money", payermoney);
								try {
									playerdata.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								playerdata.set(t.getName() + " Money", receivermoney);
								try {
									playerdata.save(file);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else {
								p.sendMessage(prefix + "§cUngültiger §aSpielername!");
							}
						}else {
							p.sendMessage(prefix + "§aDu hast §cnicht §agenug §6Gulden §aum dies zu tun!");
						}
					}else {
						p.sendMessage(prefix + "§aBitte gebe einen §cgültigen §aWert ein!");
					}
				}else {
					p.sendMessage(prefix + "§aBitte gebe einen Spielernamen ein!");
				}
			}else {
				p.sendMessage(prefix + " §aBitte benutze /pay [Spieler] [Geld]");
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("set")) {
			Long setMoney = (long) Integer.parseInt(args[1]);
			if(p.getPlayer().isOp()) {
				if(args[0] != null){
					
					Player t = p.getPlayer();
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						if(e.getPlayer().getName().equalsIgnoreCase(args[0])) {
							t = Bukkit.getServer().getPlayer(args[0]);
						}
					}
					playerdata.set(t.getName() + " Money", setMoney);
					p.sendMessage(prefix + "§aDu hast erfolreich das §6Gulden §avon §c" + t.getName() + " §aauf §6" + setMoney + " §agesetzt");
				}else {
					p.sendMessage(prefix + "§aBitte gebe einen Namen ein!");
					
				}
				try {
					playerdata.save(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				p.sendMessage(prefix + "§aGuter Versuch! Aber leider hast du §ckeine Rechte §adafür");
			}
			
			
		}
		
		if(cmd.getName().equalsIgnoreCase("add")) {
			Long addMoney = (long) Integer.parseInt(args[1]);
			if(p.getPlayer().isOp()) {
				if(args[0] != null){
					
					Player t = p.getPlayer();
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						if(e.getPlayer().getName().equalsIgnoreCase(args[0])) {
							t = Bukkit.getServer().getPlayer(args[0]);
						}
					}
					addMoney = playerdata.getLong(t.getName() + " Money") + addMoney;
					playerdata.set(t.getName() + " Money", addMoney);
					p.sendMessage(prefix + "§c" + t.getName() + " §ahat nun §c" + addMoney + " §6Gulden");
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						e.sendMessage(prefix + "§a Das Geld von §c" + t.getName() + " §a wurde auf §c" + addMoney + " §6Gulden §a gesetzt");
					}
					
					
					try {
						playerdata.save(file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					p.sendMessage(prefix + "§aBitte gebe einen Namen ein!");
					
				}
				
			}else {
				p.sendMessage(prefix + "§aGuter Versuch! Aber leider hast du §ckeine Rechte §adafür");
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("remove")) {
			Long removeMoney = (long) Integer.parseInt(args[1]);
			if(p.getPlayer().isOp()) {
				if(args[0] != null){
					
					Player t = p.getPlayer();
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						if(e.getPlayer().getName().equalsIgnoreCase(args[0])) {
							t = Bukkit.getServer().getPlayer(args[0]);
						}
					}
					removeMoney = playerdata.getLong(t.getName() + " Money") - removeMoney;
					playerdata.set(t.getName() + " Money", removeMoney);
					p.sendMessage(prefix + "§c" + t.getName() + " §ahat nun §c" + removeMoney + " §6Gulden");
					
					for(Player e : Bukkit.getServer().getOnlinePlayers()) {
						e.sendMessage(prefix + "§a Das §6Gulden §avon §c" + t.getName() + " §a wurde auf §c" + removeMoney + " §6Gulden §a gesetzt");
					}
					
					
					try {
						playerdata.save(file);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					p.sendMessage(prefix + "§aBitte gebe einen Namen ein!");
					
				}
				
			}else {
				p.sendMessage(prefix + "§aGuter Versuch! Aber leider hast du §ckeine Rechte §adafür");
			}
		}
		return true;
	}
}
