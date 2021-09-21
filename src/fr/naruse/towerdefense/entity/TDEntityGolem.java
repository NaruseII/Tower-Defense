package fr.naruse.towerdefense.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class TDEntityGolem extends AbstractEntity{
    public TDEntityGolem(Location spawn) {
        super(EntityType.IRON_GOLEM, spawn, 5);

        this.getEntity().setMaxHealth(400);
        this.getEntity().setHealth(this.getEntity().getMaxHealth());
    }

    @Override
    public double getDamage() {
        return 15;
    }
}
