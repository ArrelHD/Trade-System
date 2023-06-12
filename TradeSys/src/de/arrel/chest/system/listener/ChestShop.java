package de.arrel.chest.system.listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestShop {
	
	private Block sign;
	private Block chest;
	private int ammount;
	private int price;
	private String owner;
	
	public ChestShop(Block sign, Block chest, int ammount, int price, String owner) {
		this.sign = sign;
		this.chest = chest;
		this.ammount = ammount;
		this.price = price;
		this.owner = owner;
	}

	public Block getSign() {
		return sign;
	}

	public Block getChest() {
		return chest;
	}

	public int getAmmount() {
		return ammount;
	}

	public int getPrice() {
		return price;
	}

	public String getOwner() {
		return owner;
	}

	
	
	public static void updateSign(ChestShop shop) {
		Block chest = shop.getChest();
		Block sign = shop.getSign();
		
		
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
								if(tempDistX <= sortInv.get(y).getAmount()) { 	//Wenn die beiden Items zusammen größer (64)
									sortInv.get(x).setAmount(sortInv.get(x).getMaxStackSize());
									if(tempDistX < sortInv.get(y).getAmount()) {
										tempDistY = sortInv.get(y).getAmount() - tempDistX;
										sortInv.get(y).setAmount(tempDistY);
									}else {
										sortInv.get(y).setAmount(0);
									}
								}else {
									//Ist nicht zusammen ein ganzer STack, natürlich trz stacken
									sortInv.get(x).setAmount(sortInv.get(x).getAmount() + sortInv.get(y).getAmount());
									sortInv.get(y).setAmount(0);
								}
							}
						}
					}
				}
			}
		}
		for(int x = 0; x < sortInv.size(); x++) {	//Nimmt das veränderte Array und updated das Inventory
			chestData.getSnapshotInventory().setItem(x, sortInv.get(x));
			chestData.update();
		}
		
		
		String itemName = "";
		for(int x = ((Chest)chest.getState()).getSnapshotInventory().getSize()-1; x >= 0; x--) {	//Findet das erste Item und Setzt den Sign namen
			if(((Chest)chest.getState()).getSnapshotInventory().getItem(x) != null) {
				if(((Chest)chest.getState()).getSnapshotInventory().getItem(x).getAmount() >= shop.getAmmount()) {
					//itemName = ((Chest) chest.getState()).getSnapshotInventory().getItem(x).getItemMeta().hasDisplayName() ? ((Chest) chest.getState()).getSnapshotInventory().getItem(x).getItemMeta().getDisplayName() : ((Chest) chest.getState()).getSnapshotInventory().getItem(x).getType().toString().replace("_", " ").toLowerCase();
					//itemName = ((Chest) chest.getState()).getSnapshotInventory().getItem(x).getData().getItemType().getId();
					itemName = "" + CraftItemStack.asNMSCopy(((Chest) chest.getState()).getSnapshotInventory().getItem(x)).getName().getString();
					itemName = itemName.trim();
					//Bukkit.broadcastMessage("" + ((Chest) chest.getState()).getSnapshotInventory().getItem(x).getType());
					//e.getPlayer().sendMessage("§a" + shop.getAmmount() + "x " + ((Chest)chest.getState()).getSnapshotInventory().getItem(x).getType().NAME_TAG);
					//return;
				}
			}
		}
		Bukkit.broadcastMessage(itemName + " asd");
		
		
		
		if(itemName != "") {
			Sign ssign = (Sign)sign.getState(); //actual sign not a "block"
			ssign.setLine(0, "§a-------------");
			ssign.setLine(1, "§6" + itemName);
			ssign.setLine(2, "§a" + shop.getAmmount() + "x §6" + shop.getPrice());
			ssign.setLine(3, "§a-------------");
			ssign.update();
		} else {
			Sign ssign = (Sign)sign.getState();
			ssign.setLine(0, "§a-------------");
			ssign.setLine(1, "§cLEER");
			ssign.setLine(2, "§a" + shop.getAmmount() + "x §6" + shop.getPrice());
			ssign.setLine(3, "§a-------------");
			ssign.update();
		}
	}
	
}
