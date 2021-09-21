package fr.naruse.towerdefense.effect.particle;

import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleParam;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class FollowingParticle {

    private Entity target = null;
    private Location locationTarget = null;
    private final ParticleParam particleType;
    private final ParticleUtils.ParticleSender sender;
    private final Location start;
    private final int speed;

    private boolean isDone = false;
    private boolean stopOnTouchTarget = true;
    private boolean isOnTarget = false;

    public FollowingParticle(ParticleParam particleType, ParticleUtils.ParticleSender sender, Location start, int speed) {
        this.start = start;
        this.speed = speed;
        this.particleType = particleType;
        this.sender = sender;

        this.start.setX(TDUtils.offSet(start.getX(), 250));
        this.start.setY(TDUtils.offSet(start.getY(), 150));
        this.start.setZ(TDUtils.offSet(start.getZ(), 250));
    }

    public FollowingParticle(Entity target, ParticleParam particleType, ParticleUtils.ParticleSender sender, Location start, int speed) {
        this(particleType, sender, start, speed);
        this.target = target;
    }

    public FollowingParticle(Location target, ParticleParam particleType, ParticleUtils.ParticleSender sender, Location start, int speed) {
        this(particleType, sender, start, speed);
        this.locationTarget = target;
    }

    public void onAsyncParticleTouchTarget(Entity target) { }

    public FollowingParticle start(){
        Runnable runnable = () -> {
            if(this.isDone){
                return;
            }
            this.effect();
            this.start();
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
        return this;
    }

    public void effect(){
        if(this.target != null && this.target.isDead()){
            this.isDone = true;
            return;
        }

        boolean skipAdd = false;

        if((this.target != null && this.target.getLocation().distanceSquared(this.start) < 0.49) || (this.locationTarget != null && this.locationTarget.distanceSquared(this.start) < 0.49)){
            if(stopOnTouchTarget){
                this.isDone = true;
                this.onAsyncParticleTouchTarget(target);
                return;
            }
            this.isOnTarget = true;
            skipAdd = true;
        }

        if(!skipAdd){
            double xToAdd = Math.abs(this.start.getX()-(this.target == null ? this.locationTarget.getX() : this.target.getLocation().getX()))/this.speed;
            double yToAdd = Math.abs(this.start.getY()-(this.target == null ? this.locationTarget.getY() : this.target.getLocation().getY()))/this.speed;
            double zToAdd = Math.abs(this.start.getZ()-(this.target == null ? this.locationTarget.getZ() : this.target.getLocation().getZ()))/this.speed;
            this.start.add(this.needToAddPositive(TDUtils.Axis.X) ? xToAdd : -xToAdd,
                    this.needToAddPositive(TDUtils.Axis.Y) ? yToAdd : -yToAdd,
                    this.needToAddPositive(TDUtils.Axis.Z) ? zToAdd : -zToAdd);
        }

        sender.send(ParticleUtils.buildParticle(this.start, particleType, 0, 0, 0, 1));
    }

    private boolean needToAddPositive(TDUtils.Axis axis){
        switch (axis){
            case X:
                return !(this.start.getX()-(this.target == null ? this.locationTarget.getX() : this.target.getLocation().getX()) > 0);
            case Y:
                return !(this.start.getY()-(this.target == null ? this.locationTarget.getY() : this.target.getLocation().getY()) > 0);
            default:
                return !(this.start.getZ()-(this.target == null ? this.locationTarget.getZ() : this.target.getLocation().getZ()) > 0);
        }
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isOnTarget() {
        return isOnTarget;
    }

    public void setLocationTarget(Location locationTarget) {
        this.locationTarget = locationTarget;
        this.isOnTarget = false;
    }

    public FollowingParticle setStopOnTouchTarget(boolean stopOnTouchTarget) {
        this.stopOnTouchTarget = stopOnTouchTarget;
        return this;
    }

    public void setStart(Location start){
        this.start.setX(TDUtils.offSet(start.getX(), 250));
        this.start.setY(TDUtils.offSet(start.getY(), 150));
        this.start.setZ(TDUtils.offSet(start.getZ(), 250));
    }
}
