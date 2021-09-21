package fr.naruse.towerdefense.effect.movement;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;

public abstract class TargetEntityMovement<T extends Entity> extends FollowingEntityMovement{

    private final T target;
    private final double speed;

    public TargetEntityMovement(TowerDefensePlugin pl, Entity entity, T target) {
        this(pl, entity, target, 6);
    }

    public TargetEntityMovement(TowerDefensePlugin pl, Entity entity, T target, double speed) {
        super(pl, entity);
        this.target = target;
        this.speed = speed;
    }

    public abstract void onHit(T target) ;

    public void onRun(){}

    @Override
    public void move(Location location) {
        this.onRun();
        location = target.getLocation();
        if(location.distanceSquared(entity.getLocation()) >= NumberConversions.square(1.5)){
            double xToAdd = Math.abs(entity.getLocation().getX()-location.getX())/speed;
            double yToAdd = Math.abs(entity.getLocation().getY()-location.getY())/speed;
            double zToAdd = Math.abs(entity.getLocation().getZ()-location.getZ())/speed;

            Location location1 = entity.getLocation().add(this.needToAddPositive(TDUtils.Axis.X, location) ? xToAdd : -xToAdd,
                    this.needToAddPositive(TDUtils.Axis.Y, location) ? yToAdd : -yToAdd,
                    this.needToAddPositive(TDUtils.Axis.Z, location) ? zToAdd : -zToAdd);

            ThreadGlobal.runSync(() -> entity.setVelocity(TDUtils.genVector(entity.getLocation(), location1).normalize().multiply(0.1*factor)));
            factor+=0.1;
        }else{
            this.onHit(target);
        }
    }

    public T getTarget() {
        return target;
    }
}
