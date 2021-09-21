package fr.naruse.towerdefense.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class TDEntityArmoredZombie extends AbstractEntity {
    public TDEntityArmoredZombie(Location spawn) {
        super(EntityType.ZOMBIE, spawn, 20);

        this.getEntity().getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        this.getEntity().getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        this.getEntity().getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
        this.getEntity().setMaxHealth(25);
        this.getEntity().setHealth(this.getEntity().getMaxHealth());
    }

    @Override
    public double getDamage() {
        return 3;
    }
}
