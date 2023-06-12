package de.arrel.chest.system.listener;


import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class ChestListener implements Listener{
	private SignListener signListener;
	
	public ChestListener(SignListener sl) {
		this.signListener = sl;
		// TODO Auto-generated constructor stub
	}
	
	
	@EventHandler
	public void onChestOpen(InventoryOpenEvent e) {
		if(!e.getInventory().getType().equals(InventoryType.CHEST))
			return;
		if(e.getPlayer().isOp())
			return;
		 Block chest = e.getInventory().getLocation().getBlock();
         for(ChestShop shop : signListener.getCreatedChestShop()) {
         	if(shop.getChest().getX() == chest.getX() && shop.getChest().getY() == chest.getY() && shop.getChest().getZ() == chest.getZ()) {
         		if(!shop.getOwner().equalsIgnoreCase(e.getPlayer().getName())) {
         			e.setCancelled(true);
         			return;
         		}
         	}
         }
	}
	
	@EventHandler
	public void onChestClose(InventoryCloseEvent e){
		if(e.getInventory().getType().equals(InventoryType.CHEST)){
            Block chest = e.getInventory().getLocation().getBlock();
            for(ChestShop shop : signListener.getCreatedChestShop()) {
            	if(shop.getChest().getX() == chest.getX() && shop.getChest().getY() == chest.getY() && shop.getChest().getZ() == chest.getZ()) {
            		ChestShop.updateSign(shop);
            		return;
            	}
            }
        }
	}
	
}
