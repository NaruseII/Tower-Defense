package fr.naruse.towerdefense.inventory;

import fr.naruse.servermanager.bukkit.inventory.AbstractInventory;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryShopMain extends AbstractInventory {
    public InventoryShopMain(TowerDefensePlugin pl, Player p) {
        super(pl, p, "§lShop", 9*3);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        this.setDecoration();
        inventory.setItem(10, this.buildItem(Material.WOODEN_AXE, 0, "§l§aAxes", false, null));

        inventory.setItem(inventory.getSize() - 1, this.buildItem(Material.BARRIER, 0, "§cClose", false, null));
    }

    @Override
    protected void actionPerformed(Player player, ItemStack itemStack, InventoryAction inventoryAction, int slot) {
        if(itemStack == null){
            return;
        }

        if (slot == this.inventory.getSize() - 1) {
            p.closeInventory();
        }else if(slot == 10){
            new InventoryShopAxe((TowerDefensePlugin) this.pl, p);
        }
    }
}
