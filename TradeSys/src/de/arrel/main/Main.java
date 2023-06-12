package de.arrel.main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.arrel.chest.system.listener.AddAllowedShopOwners;
import de.arrel.chest.system.listener.BuyListener;
import de.arrel.chest.system.listener.ChestListener;
import de.arrel.chest.system.listener.SignListener;
import de.arrel.groupmoney.system.StadtCMD;

public class Main extends JavaPlugin{
	private Cmdgold moneyCmds;
	private SaveMain save;
	private StadtCMD stadtCMD;
	private SignListener signListener;
	private BuyListener buyListener;
	private AddAllowedShopOwners addAllowedShopOwners;
	
	public void onEnable() {
		save = new SaveMain();
		
		signListener = new SignListener(this);
		buyListener = new BuyListener(this);
		
		save.saveFile("plugins/WebServer/web/index.html", "BankkontoLog" , "<a href='Stadtlog.html'>Hier Klicken</a>");
		//save.log("<br>");
		moneyCmds = new Cmdgold(this);
		stadtCMD = new StadtCMD(this);
		addAllowedShopOwners = new AddAllowedShopOwners(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new Eventlistner(), this);
		pm.registerEvents(signListener, this);
		pm.registerEvents(buyListener, this);
		pm.registerEvents(new ChestListener(signListener), this);
		
		JoinLeaveCmd joinLeaveCMD = new JoinLeaveCmd();
		
		this.getCommand("join").setExecutor(joinLeaveCMD);
		this.getCommand("leave").setExecutor(joinLeaveCMD);
		
		this.getCommand("money").setExecutor(moneyCmds);
		this.getCommand("pay").setExecutor(moneyCmds);
		this.getCommand("set").setExecutor(moneyCmds);
		this.getCommand("add").setExecutor(moneyCmds);
		this.getCommand("remove").setExecutor(moneyCmds);
		this.getCommand("stadt").setExecutor(stadtCMD);
		this.getCommand("addShop").setExecutor(addAllowedShopOwners);
		System.out.print("Das TradePlugin wurde erfolgreich aktiviert!");
	}


	public SaveMain getSave() {
		return save;
	}


	public SignListener getSignListener() {
		return signListener;
	}


	public BuyListener getBuyListener() {
		return buyListener;
	}


	public StadtCMD getStadtCMD() {
		return stadtCMD;
	}


	public AddAllowedShopOwners getAddAllowedShopOwners() {
		return addAllowedShopOwners;
	}
	
	
	
	
	
}
