package fr.naruse.towerdefense.effect;

import fr.naruse.towerdefense.effect.movement.AbstractEntityMovement;
import fr.naruse.towerdefense.effect.movement.TargetEntityMovement;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import net.minecraft.core.particles.ParticleParam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

public class EffectFloatingItem extends Runner implements IEffect {

    private final TowerDefensePlugin pl;
    private Item entity;
    private final Location location;
    private final ParticleParam feetParticle;
    private AbstractEntityMovement entityMovement;

    public EffectFloatingItem(TowerDefensePlugin pl, Location location, Location spawnLoc, ItemStack itemStack, ParticleParam feetParticle) {
        this(pl, location, spawnLoc, itemStack, feetParticle, null);
    }

    public EffectFloatingItem(TowerDefensePlugin pl, Location location, Location spawnLoc, ItemStack itemStack, ParticleParam feetParticle, Class<? extends AbstractEntityMovement> clazz) {
        this.pl = pl;
        this.location = location;
        this.feetParticle = feetParticle;

        Runnable runnable = () -> {
            this.entity = location.getWorld().dropItem(spawnLoc, itemStack);
            this.entity.setInvulnerable(true);
            this.entity.setGravity(false);
            this.entity.setWillAge(false);

            try {
                if(clazz != null){
                    this.entityMovement = clazz.getConstructor(TowerDefensePlugin.class, Entity.class).newInstance(pl, entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            preStart();
            start();
        };
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(runnable);
        }else{
            runnable.run();
        }
    }

    public void preStart() { }

    public void onRun() { }

    private Location lastLocation;
    @Override
    public void run() {
        if(entity == null || entity.isDead()){
            setCancelled(true);
            return;
        }

        if(lastLocation != null && TDUtils.distanceSquared(lastLocation, entity.getLocation()) > NumberConversions.square(5)){
            setCancelled(true);
            return;
        }
        lastLocation = entity.getLocation();

        if(entityMovement != null){
            entityMovement.move(location.clone().add(0, 0.5, 0));
        }

        if(feetParticle != null){
            ParticleUtils.buildParticle(entity.getLocation(), feetParticle, 0.1f, 0.1f, 0.1f, 1, 0).toNearbyFifty();
        }

        if(entityMovement == null || !(entityMovement instanceof TargetEntityMovement)){
            onRun();
        }
    }

    @Override
    public void kill() {
        if(!isCancelled()){
            setCancelled(true);
            return;
        }
        if(this.entity.isDead()){
            return;
        }
        if(Bukkit.getServer().isPrimaryThread()){
            this.entity.remove();
        }else{
            ThreadGlobal.runSync(pl, () -> this.entity.remove());
        }
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
        kill();
    }

    public Item getEntity() {
        return entity;
    }

    public void setEntityMovement(AbstractEntityMovement entityMovement) {
        this.entityMovement = entityMovement;
    }

    public AbstractEntityMovement getEntityMovement() {
        return entityMovement;
    }
}

