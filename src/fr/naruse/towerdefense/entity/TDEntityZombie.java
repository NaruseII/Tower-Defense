package fr.naruse.towerdefense.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class TDEntityZombie extends AbstractEntity{
    public TDEntityZombie(Location spawn) {
        super(EntityType.ZOMBIE, spawn, 20);
    }

    @Override
    public double getDamage() {
        return 1;
    }
}
