package fr.naruse.towerdefense.inventory;

import com.google.common.collect.Lists;
import fr.naruse.servermanager.bukkit.inventory.AbstractInventory;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.unit.AbstractUnit;
import fr.naruse.towerdefense.unit.UnitCentral;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryUnitMain extends AbstractInventory {

    private final AbstractUnit unit;

    public InventoryUnitMain(JavaPlugin pl, Player p, AbstractUnit unit) {
        super(pl, p, "§lUnit Inventory", 9*3, false);
        this.unit = unit;

        this.initInventory(this.inventory);
        p.openInventory(this.inventory);
    }

    @Override
    protected void initInventory(Inventory inventory) {
        this.setDecoration();
        Levels.Level nextLevel = this.unit.getLevels().getNextLevel();
        if(nextLevel != null){
            inventory.setItem(10, this.buildItem(Material.ENCHANTED_BOOK, 0, "§aLevel: §2"+this.unit.getLevels().getCurrentLevel().getLevel(), false, Lists.newArrayList(
                    "§7[Click to level up]",
                    "§7Cost: §6"+ nextLevel.getGoldCost() +" gold §f| §b"+ nextLevel.getWoodCost() +" wood §f| §3"+ nextLevel.getStoneCost() +" stone",
                    "§dHealth: §5"+this.unit.getMaxHealth() +" §d > §5"+nextLevel.getValue(LevelAttribute.MAX_HEALTH)
            )));
        }
        inventory.setItem(13, this.buildItem(Material.TOTEM_OF_UNDYING, 0, "§dHealth: §5"+unit.getHealth()+"/"+unit.getMaxHealth()+" §c❤", false, null));
        inventory.setItem(16, this.buildItem(Material.SHEARS, 0, "§cDestroy", false, null));

        inventory.setItem(inventory.getSize() - 1, this.buildItem(Material.BARRIER, 0, "§cClose", false, null));
    }

    @Override
    protected void actionPerformed(Player player, ItemStack itemStack, InventoryAction inventoryAction, int slot) {
        if (itemStack != null) {
            if (slot == this.inventory.getSize() - 1) {
                p.closeInventory();
            }else if(slot == 10){
                Runnable runnable = () -> {
                    this.unit.upgrade(p);
                    ThreadGlobal.runSync(() -> {
                        if(!isDone()){
                            inventory.clear();
                            initInventory(inventory);
                        }
                    });
                };
                CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
            }else if(slot == 16){
                if(unit instanceof UnitCentral){
                    p.sendMessage("§cYou can't destroy the main unit!");
                    return;
                }
                p.closeInventory();

                Levels.Level level = this.unit.getLevels().getCurrentLevel();
                ResourcesStorage.addGold(level.getGoldCost()/2);
                ResourcesStorage.addWood(level.getWoodCost()/2);
                ResourcesStorage.addStone(level.getStoneCost()/2);

                this.unit.kill();
                this.unit.newInstance().giveItem(p);
            }
        }
    }
}
