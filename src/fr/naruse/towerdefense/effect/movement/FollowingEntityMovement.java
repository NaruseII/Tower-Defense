package fr.naruse.towerdefense.effect.movement;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class FollowingEntityMovement extends AbstractEntityMovement {

    public FollowingEntityMovement(TowerDefensePlugin pl, Entity entity) {
        super(pl, entity);
    }

    private double distance = 2;
    public FollowingEntityMovement(TowerDefensePlugin pl, Entity entity, double distance) {
        super(pl, entity);
        this.distance = distance;
    }

    protected Vector lastVector;
    protected double factor = 1;
    protected double offSetX = TDUtils.RANDOM.nextDouble()/(TDUtils.RANDOM.nextBoolean() ? -2 : 2);
    protected double offSetY = TDUtils.RANDOM.nextDouble()/(TDUtils.RANDOM.nextBoolean() ? -2 : 2);
    protected double offSetZ = TDUtils.RANDOM.nextDouble()/(TDUtils.RANDOM.nextBoolean() ? -2 : 2);

    @Override
    public void move(Location location) {
        location.add(offSetX, offSetY, offSetZ);
        if(location.distanceSquared(entity.getLocation()) >= NumberConversions.square(distance)){
            double xToAdd = Math.abs(entity.getLocation().getX()-location.getX())/6;
            double yToAdd = Math.abs(entity.getLocation().getY()-location.getY())/6;
            double zToAdd = Math.abs(entity.getLocation().getZ()-location.getZ())/6;

            Location location1 = entity.getLocation().add(this.needToAddPositive(TDUtils.Axis.X, location) ? xToAdd : -xToAdd,
                    this.needToAddPositive(TDUtils.Axis.Y, location) ? yToAdd : -yToAdd,
                    this.needToAddPositive(TDUtils.Axis.Z, location) ? zToAdd : -zToAdd);

            ThreadGlobal.runSync(() -> entity.setVelocity(lastVector = TDUtils.genVector(entity.getLocation(), location1).normalize().multiply(0.1*factor)));
            factor+=0.1;
        }else if(lastVector != null){
            ThreadGlobal.runSync(() -> entity.setVelocity(lastVector.multiply(0.75)));
            factor = 1;
        }
    }
}
