package fr.naruse.towerdefense.effect;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;
import java.util.UUID;

public class EffectItemDropper extends Runner implements IEffect{

    private final Set<Item> itemSet = Sets.newHashSet();

    private final Location location;
    private final Material material;
    private final int tickInterval;

    public EffectItemDropper(Location location, Material material, int tickInterval) {
        this.location = location;
        this.material = material;
        this.tickInterval = tickInterval;
        this.start();
    }

    @Override
    public void kill() {
        this.setCancelled(true);
        ThreadGlobal.runSync(() -> {
            for (Item item : itemSet) {
                item.remove();
            }
        });
    }

    private int tick = 0;

    @Override
    public void run() {
        if(tick < tickInterval){
            tick++;
            return;
        }
        tick = 0;

        ThreadGlobal.runSync(() -> {
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(UUID.randomUUID().toString());
            itemStack.setItemMeta(meta);

            Item item = location.getWorld().dropItemNaturally(location, itemStack);
            itemSet.add(item);
            ThreadGlobal.runSyncLater(() -> {
                item.remove();
                itemSet.remove(item);
            }, 20);
        });
    }
}
