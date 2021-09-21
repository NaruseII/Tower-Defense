package fr.naruse.towerdefense.storage;

import com.google.common.collect.Maps;
import fr.naruse.towerdefense.utils.world.TDRock;
import fr.naruse.towerdefense.utils.world.TDTree;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Axe {

    public static final AxeItem WOODEN_AXE = new AxeItem(Material.WOODEN_AXE, 20);
    public static final AxeItem STONE_AXE = new AxeItem(Material.STONE_AXE, 15);
    public static final AxeItem IRON_AXE = new AxeItem(Material.IRON_AXE, 10);
    public static final AxeItem GOLDEN_AXE = new AxeItem(Material.GOLDEN_AXE, 8);
    public static final AxeItem DIAMOND_AXE = new AxeItem(Material.DIAMOND_AXE, 5);
    public static final AxeItem NETHERITE_AXE = new AxeItem(Material.NETHERITE_AXE, 1);

    public static final AxeItem[] ALL = new AxeItem[]{WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE};

    public static boolean dig(Player p, ItemStack itemStack, Block block){
        AxeItem axeItem = AxeItem.getAxeItem(itemStack.getType());
        if(axeItem == null){
            return false;
        }

        TDTree tree = WorldUtils.findTreeByBlock(block);
        if(tree == null){
            TDRock rock = WorldUtils.findRockByBlock(block);
            if(rock == null){
                return false;
            }

            axeItem.dig(p, rock);
        }else{
            axeItem.dig(p, tree);
        }

        return true;
    }

    public static final class AxeItem {

        private static final Map<Material, AxeItem> axeByTypeMap = Maps.newHashMap();

        public static AxeItem getAxeItem(Material material) {
            return axeByTypeMap.get(material);
        }

        private final Map<Player, Long> lastTreeDigMap = Maps.newHashMap();
        private final Map<Player, Long> lastRockDigMap = Maps.newHashMap();

        private final Material material;
        private final int tickIntervalInMs;

        public AxeItem(Material material, int tickInterval) {
            axeByTypeMap.put(material, this);
            this.material = material;
            this.tickIntervalInMs = tickInterval*50;
        }

        public void dig(Player p, TDTree tree) {
            if(!this.lastTreeDigMap.containsKey(p)){
                this.lastTreeDigMap.put(p, 0l);
            }
            Long l = this.lastTreeDigMap.get(p);
            if(System.currentTimeMillis()-l >= this.tickIntervalInMs){
                this.lastTreeDigMap.put(p, System.currentTimeMillis());

                ResourcesStorage.addWood(1);
                tree.digEffect();
            }
        }

        public void dig(Player p, TDRock rock) {
            if(!this.lastRockDigMap.containsKey(p)){
                this.lastRockDigMap.put(p, 0l);
            }
            Long l = this.lastRockDigMap.get(p);
            if(System.currentTimeMillis()-l >= this.tickIntervalInMs){
                this.lastRockDigMap.put(p, System.currentTimeMillis());

                ResourcesStorage.addStone(1);
                rock.digEffect();
            }
        }

        public boolean possesIt(Player p){
            return p.getInventory().contains(material);
        }

        public void give(Player p){
            for (AxeItem axeItem : ALL) {
                axeItem.remove(p);
            }
            p.getInventory().setItem(0, new ItemStack(this.material));
        }

        private void remove(Player p){
            p.getInventory().remove(this.material);
        }

    }

}
