package fr.naruse.towerdefense.effect.movement;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.TDUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class AbstractEntityMovement {

    protected final TowerDefensePlugin pl;
    protected final Entity entity;

    public AbstractEntityMovement(TowerDefensePlugin pl, Entity entity) {
        this.pl = pl;
        this.entity = entity;
    }

    public abstract void move(Location location);

    protected boolean needToAddPositive(TDUtils.Axis axis, Location location){
        switch (axis){
            case X:
                return !(entity.getLocation().getX()-location.getX() > 0);
            case Y:
                return !(entity.getLocation().getY()-location.getY() > 0);
            default:
                return !(entity.getLocation().getZ()-location.getZ() > 0);
        }
    }
}
