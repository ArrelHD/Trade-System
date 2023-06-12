package de.arrel.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinLeaveCmd implements CommandExecutor{

	private Player[] team;

	public JoinLeaveCmd() {
		team = new Player[2];
		//team[0-1]
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		// /join [..]

		if(!(sender instanceof Player)) {
			sender.sendMessage("§cDu bist kein Spieler");
			return true;
		}

		if(arg.equalsIgnoreCase("join")) {
			for(int i = 0; i < team.length; i++) {
				if(team[i] == (Player)sender) {
					sender.sendMessage("§cDu bist bereits im Team");
					return true;
				}
			}
			for(int i = 0; i < team.length; i++) {
				if(team[i] == null) {
					team[i] = (Player) sender;
					sender.sendMessage("§aDu bist jetzt im Team");
					for(int j = 0; j < team.length; j++) {
						if(team[j] != null && team[j] != (Player)sender) {
							team[j].sendMessage("§aDer Spieler " + sender.getName() + " ist deinem Team beigetreten");
						}
					}
					return true;
				}
			}

		}else if(arg.equalsIgnoreCase("leave")) {
			for(int i = 0; i < team.length; i++) {
				if(team[i] == (Player)sender) {
					team[i] = null;
					sender.sendMessage("§aDu wurdest aus dem Team entfernt");
					return true;
				}
			}
			sender.sendMessage("§cDu bist nicht im Team");
		}

		return true;
	}

}
