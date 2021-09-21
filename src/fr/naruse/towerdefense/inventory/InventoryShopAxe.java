package fr.naruse.towerdefense.inventory;

import com.google.common.collect.Lists;
import fr.naruse.servermanager.bukkit.inventory.AbstractInventory;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.Axe;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryShopAxe extends AbstractInventory {

    private static final int STONE_AXE = 300;
    private static final int IRON_AXE = 500;
    private static final int GOLDEN_AXE = 1000;
    private static final int DIAMOND_AXE = 1500;
    private static final int NETHERITE_AXE = 3000;

    public InventoryShopAxe(TowerDefensePlugin pl, Player p) {
        super(pl, p, "§lShop - Axes", 9*3);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        this.setDecoration();

        inventory.setItem(10, this.buildItem(Material.STONE_AXE, 0, "§lStone Axe", false, Lists.newArrayList("§7Cost: §6"+STONE_AXE+" gold")));
        inventory.setItem(11, this.buildItem(Material.IRON_AXE, 0, "§7§lIron Axe", false, Lists.newArrayList("§7Cost: §6"+IRON_AXE+" gold")));
        inventory.setItem(12, this.buildItem(Material.GOLDEN_AXE, 0, "§6§lGolden Axe", false, Lists.newArrayList("§7Cost: §6"+GOLDEN_AXE+" gold")));
        inventory.setItem(13, this.buildItem(Material.DIAMOND_AXE, 0, "§b§lDiamond Axe", false, Lists.newArrayList("§7Cost: §6"+DIAMOND_AXE+" gold")));
        inventory.setItem(14, this.buildItem(Material.NETHERITE_AXE, 0, "§4§lNetherite Axe", false, Lists.newArrayList("§7Cost: §6"+NETHERITE_AXE+" gold")));

        inventory.setItem(inventory.getSize() - 1, this.buildItem(Material.BARRIER, 0, "§cBack", false, null));
    }

    @Override
    protected void actionPerformed(Player player, ItemStack itemStack, InventoryAction inventoryAction, int slot) {
        if(itemStack == null){
            return;
        }

        if (slot == this.inventory.getSize() - 1) {
            new InventoryShopMain((TowerDefensePlugin) this.pl, p);
        }else if(slot == 10){
            p.closeInventory();
            if(ResourcesStorage.getGold() < STONE_AXE){
                p.sendMessage("§cYou do not have enough gold!");
            }else{
                ResourcesStorage.addGold(-STONE_AXE);
                Axe.STONE_AXE.give(p);
                p.sendMessage("§aProduct sent!");
            }
        }else if(slot == 11){
            if (Axe.IRON_AXE.possesIt(p)) {
                p.sendMessage("§cYou already have bought that axe!");
                return;
            }

            p.closeInventory();
            if(ResourcesStorage.getGold() < IRON_AXE){
                p.sendMessage("§cYou do not have enough gold!");
            }else{
                ResourcesStorage.addGold(-IRON_AXE);
                Axe.IRON_AXE.give(p);
                p.sendMessage("§aProduct sent!");
            }
        } else if(slot == 12){
            if (Axe.GOLDEN_AXE.possesIt(p)) {
                p.sendMessage("§cYou already have bought that axe!");
                return;
            }

            p.closeInventory();
            if(ResourcesStorage.getGold() < GOLDEN_AXE){
                p.sendMessage("§cYou do not have enough gold!");
            }else{
                ResourcesStorage.addGold(-GOLDEN_AXE);
                Axe.GOLDEN_AXE.give(p);
                p.sendMessage("§aProduct sent!");
            }
        }else if(slot == 13){
            if (Axe.DIAMOND_AXE.possesIt(p)) {
                p.sendMessage("§cYou already have bought that axe!");
                return;
            }

            p.closeInventory();
            if(ResourcesStorage.getGold() < DIAMOND_AXE){
                p.sendMessage("§cYou do not have enough gold!");
            }else{
                ResourcesStorage.addGold(-DIAMOND_AXE);
                Axe.DIAMOND_AXE.give(p);
                p.sendMessage("§aProduct sent!");
            }
        }else if(slot == 14){
            if (Axe.NETHERITE_AXE.possesIt(p)) {
                p.sendMessage("§cYou already have bought that axe!");
                return;
            }

            p.closeInventory();
            if(ResourcesStorage.getGold() < NETHERITE_AXE){
                p.sendMessage("§cYou do not have enough gold!");
            }else{
                ResourcesStorage.addGold(-NETHERITE_AXE);
                Axe.NETHERITE_AXE.give(p);
                p.sendMessage("§aProduct sent!");
            }
        }
    }
}
