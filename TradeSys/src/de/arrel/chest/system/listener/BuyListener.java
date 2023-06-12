package de.arrel.chest.system.listener;

import de.arrel.groupmoney.system.StadtCMD;
import de.arrel.chest.system.listener.assets.*;
import de.arrel.main.Main;
import de.arrel.main.SaveMain;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BuyListener implements Listener{
	private Main main;
	private SaveMain save;
	private SignListener signListener;
	
	private String path;
	
	private ArrayList<ChestShop> createdChestShop = new ArrayList<ChestShop>();

	public BuyListener(Main main) {
		this.main = main;
		this.save = main.getSave();
		this.signListener = main.getSignListener();
		path = signListener.getPath();
		signListener.get();
		get();
	}
	
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null) {
			return;
		}
		if("LEGACY_WALL_SIGN".equalsIgnoreCase(e.getClickedBlock().getType()+"")) {
			get();
			//return;
			
			for(int i = 0; i < createdChestShop.size(); i++) {
				if(e.getClickedBlock().getLocation().equals(createdChestShop.get(i).getSign().getLocation())) {
					
					ChestShop shop = createdChestShop.get(i);
					
					Location signLoc = shop.getSign().getLocation();
					Location chestLoc = shop.getChest().getLocation();
					
					Block sign = signLoc.getBlock();
					Block chest = chestLoc.getBlock();
					
					
					if(sign.equals(e.getClickedBlock())) {
						if(e.getPlayer().getName().equalsIgnoreCase(shop.getOwner())) {
							e.getPlayer().sendMessage("$cDas ist dein eigener Shop!.");
							return;
						}
						
						int transfermoney = shop.getPrice();
						int recivermoney = Integer.parseInt(save.getStringFile(main.getStadtCMD().getUserMoneyPath(), shop.getOwner() + " Money"));
						int sendermoney = Integer.parseInt(save.getStringFile(main.getStadtCMD().getUserMoneyPath(), e.getPlayer().getName() + " Money"));

						if(sendermoney < transfermoney) { //$berpruefung ob K§ufer genug Geld hat.
							e.getPlayer().sendMessage("$cDu hast dazu nicht genug Geld.");
							return;
						}
						
						if(e.getPlayer().getInventory().firstEmpty() == -1){ //$berpruefung ob K§ufer Inventar voll ist.
							e.getPlayer().sendMessage("$cDein Inventar ist voll.");
							return;
						}
						
						double mwst = 0.15;
						sendermoney -= transfermoney;
						int mwstMoney = (int) (transfermoney*mwst);
						recivermoney += (transfermoney-mwstMoney);
						boolean isEmpty = true;
						Chest chestData = (Chest) chest.getState();

						
						//chestData.getSnapshotInventory().all 
						Inventory chestInv = chestData.getSnapshotInventory();
						chestData.getSnapshotInventory().getSize();
						ArrayList<ItemStack> sortInv = new ArrayList<>();
						
						for(int o = 0; o < chestInv.getSize(); o++) {
							sortInv.add(chestInv.getItem(o));
						}
						int tempDistX = 0;
						int tempDistY = 0;
						
						for(int x = 0; x < sortInv.size(); x++) { //Stackt Items Algorithmus
							if(sortInv.get(x) != null) {
								if(sortInv.get(x).getAmount() != sortInv.get(x).getMaxStackSize()) {
									for(int y = x+1; y < sortInv.size(); y++) {
										if(sortInv.get(y) != null && sortInv.get(y).getType().getId() == sortInv.get(x).getType().getId()) {
											if(sortInv.get(x).getAmount() != sortInv.get(x).getMaxStackSize() && sortInv.get(y).getAmount() != sortInv.get(y).getMaxStackSize()) {
												tempDistX = sortInv.get(x).getMaxStackSize() - sortInv.get(x).getAmount();
												if(tempDistX <= sortInv.get(y).getAmount()) { 	//Wenn die beiden Items zusammen gr$$er (64)
													sortInv.get(x).setAmount(sortInv.get(x).getMaxStackSize());
													if(tempDistX < sortInv.get(y).getAmount()) {
														tempDistY = sortInv.get(y).getAmount() - tempDistX;
														sortInv.get(y).setAmount(tempDistY);
													}else {
														sortInv.get(y).setAmount(0);
													}
												}else {
													//Ist nicht zusammen ein ganzer STack, nat§rlich trz stacken
													sortInv.get(x).setAmount(sortInv.get(x).getAmount() + sortInv.get(y).getAmount());
													sortInv.get(y).setAmount(0);
												}
											}
										}
									}
								}
							}
						}
						for(int x = 0; x < sortInv.size(); x++) {	//Nimmt das ver§nderte Array und updated das Inventory
							chestData.getSnapshotInventory().setItem(x, sortInv.get(x));
							chestData.update();
						}
						
						
						ItemStack tempItem = null;
						for(int c = 0; c < chestData.getSnapshotInventory().getSize(); c++) { 
							if(chestData.getSnapshotInventory().getItem(c) != null) {
								tempItem = chestData.getSnapshotInventory().getItem(c);
								if(tempItem.getAmount() == shop.getAmmount()) {
									e.getPlayer().getInventory().addItem(tempItem);
									
									tempItem.setAmount(0);
									chestData.getSnapshotInventory().setItem(c, tempItem);
									chestData.update();

									
									isEmpty = false;
									break;
								}else if(tempItem.getAmount() >= shop.getAmmount()) {
									tempItem.setAmount(tempItem.getAmount()-shop.getAmmount());
									chestData.getSnapshotInventory().setItem(c, tempItem);
									chestData.update();
									
									tempItem.setAmount(shop.getAmmount());
									e.getPlayer().getInventory().addItem(tempItem);
									
									isEmpty = false;
									break;
								}
							}
						}
						 
						
						
						
						if(!isEmpty) {
							save.saveFile(main.getStadtCMD().getUserMoneyPath(), e.getPlayer().getName() + " Money", sendermoney); //SenderMoney festlegen
							save.saveFile(main.getStadtCMD().getUserMoneyPath(), shop.getOwner() + " Money", recivermoney); //ReciverMoney festlegen
							save.saveFile(StadtCMD.getStadtPath(), "money", Integer.parseInt(save.getStringFile(StadtCMD.getStadtPath(), "money"))+mwstMoney);	//mwst adden zur stadt
							e.getPlayer().sendMessage("$aDu hast erfolgreich das Item f§r $6" + transfermoney +"Gulden§a gekauft.");
							save.log("Beim handel von dem K§ufer " + e.getPlayer().getName() + " und Verk§ufer " + shop.getOwner() + " ist " + mwstMoney + "Gulden Mwst. gezahlt worden. <br>");
							
							//e.getClickedBlock()
							
							
							
							for(Player t : Bukkit.getOnlinePlayers()) {
								if(t.getName().equalsIgnoreCase(shop.getOwner())) {
									t.sendMessage("$aDer Spieler " + e.getPlayer().getName() + " hat ein Item f§r $6" + shop.getPrice() + "Gulden incl.MwSt $a gekauft");
								}
							}
						}else {
							e.getPlayer().sendMessage("$aLeider ist dieser Shop leer.");
						}
						
						
					}
					ChestShop.updateSign(shop);
					return;
				}
			}
			
			/*
			int i = 0;
			while(save.getBooleanFile(path, i + ".created")) {
				if(save.getStringFile(path, i + ".signloc.world") == null) {
					return;
				}
				Location signLoc = new Location(Bukkit.getWorld(save.getStringFile(path, i + ".signloc.world")), Integer.parseInt(save.getStringFile(path, i + ".signloc.x")), Integer.parseInt(save.getStringFile(path, i + ".signloc.y")), Integer.parseInt(save.getStringFile(path, i + ".signloc.z")));
				Location chestLoc = new Location(Bukkit.getWorld(save.getStringFile(path, i + ".chestloc.world")), Integer.parseInt(save.getStringFile(path, i + ".chestloc.x")), Integer.parseInt(save.getStringFile(path, i + ".chestloc.y")), Integer.parseInt(save.getStringFile(path, i + ".chestloc.z")));
				
				if(e.getClickedBlock().getLocation().equals(signLoc)) {
					Block sign = signLoc.getBlock();
					Block chest = chestLoc.getBlock();
					
					if(e.getAction().equals(org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)) {
						for(int x = 0; x < ((Chest)chest.getState()).getSnapshotInventory().getSize(); x++) {	//Findet das erste Item und Setzt den Sign namen
							if(((Chest)chest.getState()).getSnapshotInventory().getItem(x) != null) {
								if(((Chest)chest.getState()).getSnapshotInventory().getItem(x).getAmount() >= Integer.parseInt(save.getStringFile(path, i + ".ammount"))) {
									e.getPlayer().sendMessage("$a" + "5" + "x " + ((Chest)chest.getState()).getSnapshotInventory().getItem(x).getType().name());
									return;
								}
							}
						}
						e.getPlayer().sendMessage("$a" + "5" + "x LEER");
						return;
					}
					
					for(int x = 0; x < ((Chest)chest.getState()).getSnapshotInventory().getSize(); x++) {	//Findet das erste Item und Setzt den Sign namen
						if(((Chest)chest.getState()).getSnapshotInventory().getItem(x) != null) {
							((Sign) sign.getState()).setLine(1, "$a" + "5" + "x " + ((Chest)chest.getState()).getSnapshotInventory().getItem(x).getType().name());
							//e.getPlayer().sendMessage("$a" + Integer.parseInt(save.getStringFile(path, i + ".ammount")) + "x " + ((Chest)chest.getState()).getSnapshotInventory().getItem(x).getType().name());
							break;
						}
					}
					if(((Chest)chest.getState()).getSnapshotInventory().firstEmpty() == -1) {
						((Sign) sign.getState()).setLine(1, "$a" + Integer.parseInt(save.getStringFile(path, i + ".ammount")) + "x [LEER]");
					}
					
					((Sign) sign.getState()).update();
					
					if(sign.equals(e.getClickedBlock())) {
						//Ist ein Registrierter ChestShop
						if(e.getPlayer().getName().equalsIgnoreCase(save.getStringFile(path, i + ".owner"))) {
							e.getPlayer().sendMessage("$cDas ist dein eigener Shop!.");
							return;
						}
						
						int transfermoney = Integer.parseInt(save.getStringFile(path, i + ".price"));
						int recivermoney = Integer.parseInt(save.getStringFile(main.getStadtCMD().getUserMoneyPath(), save.getStringFile(path, i + ".owner") + " Money"));
						int sendermoney = Integer.parseInt(save.getStringFile(main.getStadtCMD().getUserMoneyPath(), e.getPlayer().getName() + " Money"));
						
						int ammount = Integer.parseInt(save.getStringFile(path, i + ".ammount"));
						
						if(sendermoney < transfermoney) { //$berpruefung ob K§ufer genug Geld hat.
							e.getPlayer().sendMessage("$cDu hast dazu nicht genug Geld.");
							return;
						}
						
						if(e.getPlayer().getInventory().firstEmpty() == -1){ //$berpruefung ob K§ufer Inventar voll ist.
							e.getPlayer().sendMessage("$cDein Inventar ist voll.");
							return;
						}

						double mwst = 0.2;
						sendermoney -= transfermoney;
						recivermoney += (transfermoney-transfermoney*mwst);
						int mwstMoney = (int) (transfermoney*mwst);
						boolean isEmpty = true;
						Chest chestData = (Chest) chest.getState();

						
						//chestData.getSnapshotInventory().all 
						Inventory chestInv = chestData.getSnapshotInventory();
						chestData.getSnapshotInventory().getSize();
						ArrayList<ItemStack> sortInv = new ArrayList<>();
						
						for(int o = 0; o < chestInv.getSize(); o++) {
							sortInv.add(chestInv.getItem(o));
						}
						int tempDistX = 0;
						int tempDistY = 0;
						
						for(int x = 0; x < sortInv.size(); x++) { //Stackt Items Algorithmus
							if(sortInv.get(x) != null) {
								if(sortInv.get(x).getAmount() != sortInv.get(x).getMaxStackSize()) {
									for(int y = x+1; y < sortInv.size(); y++) {
										if(sortInv.get(y) != null) {
											if(sortInv.get(x).getAmount() != sortInv.get(x).getMaxStackSize() && sortInv.get(y).getAmount() != sortInv.get(y).getMaxStackSize()) {
												tempDistX = sortInv.get(x).getMaxStackSize() - sortInv.get(x).getAmount();
												if(tempDistX <= sortInv.get(y).getAmount()) { 	//Wenn die beiden Items zusammen gr$$er (64)
													sortInv.get(x).setAmount(sortInv.get(x).getMaxStackSize());
													if(tempDistX < sortInv.get(y).getAmount()) {
														tempDistY = sortInv.get(y).getAmount() - tempDistX;
														sortInv.get(y).setAmount(tempDistY);
													}else {
														sortInv.get(y).setAmount(0);
													}
												}else {
													//Ist nicht zusammen ein ganzer STack, nat§rlich trz stacken
													sortInv.get(x).setAmount(sortInv.get(x).getAmount() + sortInv.get(y).getAmount());
													sortInv.get(y).setAmount(0);
												}
											}
										}
									}
								}
							}
						}
						for(int x = 0; x < sortInv.size(); x++) {	//Nimmt das ver§nderte Array und updated das Inventory
							chestData.getSnapshotInventory().setItem(x, sortInv.get(x));
							chestData.update();
						}
						//Sheep test;
						//if(true)
						//	return;
						ItemStack tempItem = null;
						for(int c = 0; c < chestData.getSnapshotInventory().getSize(); c++) { 
							if(chestData.getSnapshotInventory().getItem(c) != null) {
								tempItem = chestData.getSnapshotInventory().getItem(c);
								if(tempItem.getAmount() == ammount) {
									e.getPlayer().getInventory().addItem(tempItem);
									
									tempItem.setAmount(0);
									chestData.getSnapshotInventory().setItem(c, tempItem);
									chestData.update();

									
									isEmpty = false;
									break;
								}else if(tempItem.getAmount() >= ammount) {
									tempItem.setAmount(tempItem.getAmount()-ammount);
									chestData.getSnapshotInventory().setItem(c, tempItem);
									chestData.update();
									
									tempItem.setAmount(ammount);
									e.getPlayer().getInventory().addItem(tempItem);
									
									isEmpty = false;
									break;
								}
							}
						}
						
						
						
						
						if(!isEmpty) {
							save.saveFile(main.getStadtCMD().getUserMoneyPath(), e.getPlayer().getName() + " Money", sendermoney); //SenderMoney festlegen
							save.saveFile(main.getStadtCMD().getUserMoneyPath(), save.getStringFile(path, i + ".owner") + " Money", recivermoney); //ReciverMoney festlegen
							save.saveFile(StadtCMD.getStadtPath(), "money", Integer.parseInt(save.getStringFile(StadtCMD.getStadtPath(), "money"))+mwstMoney);
							e.getPlayer().sendMessage("$aDu hast erfolgreich das Item f§r $6" + save.getStringFile(path, i + ".price") +"Gulden§a gekauft.");
							save.log("Beim handel von dem K§ufer " + e.getPlayer().getName() + " und Verk§ufer " + save.getStringFile(path, i + ".owner") + " ist " + mwstMoney + "Gulden Mwst. gezahlt worden." );
							
							for(Player t : Bukkit.getOnlinePlayers()) {
								if(t.getName().equalsIgnoreCase(save.getStringFile(path, i + ".owner"))) {
									t.sendMessage("$aDer Spieler " + e.getPlayer().getName() + " hat ein Item f§r $6" + save.getStringFile(path, i + ".price") + "Gulden incl.MwSt $a gekauft");
								}
							}
						}else {
							e.getPlayer().sendMessage("$aLeider ist dieser Shop leer.");
						}
						
						for(int x = 0; x < chestData.getSnapshotInventory().getSize(); x++) {	//Findet das erste Item und Setzt den Sign namen
							if(chestData.getSnapshotInventory().getItem(x) != null) {
								((Sign) sign.getState()).setLine(1, "$a" + ammount + "x " + chestData.getSnapshotInventory().getItem(x).getType().name());
								break;
							}
						}
						if(chestData.getSnapshotInventory().firstEmpty() == -1) {
							((Sign) sign.getState()).setLine(1, "$a" + ammount + "x [LEER]");
						}
						
						return;
					}
					
					i++;
				}
			}*/
		}
	}
	
	
	public void get() {
		createdChestShop = signListener.getCreatedChestShop();
	}
	
}
