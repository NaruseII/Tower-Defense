package fr.naruse.towerdefense.effect;

import fr.naruse.towerdefense.effect.movement.AbstractEntityMovement;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class EffectThrowableEntity implements IEffect {

    private final TowerDefensePlugin pl;
    private final LivingEntity owner;
    private final Entity entity;

    private boolean isPickedUp = false;
    private boolean isOnGround = false;
    private AbstractEntityMovement entityMovement = null;

    public EffectThrowableEntity(TowerDefensePlugin pl, LivingEntity owner, EntityType entityType, Vector vector) {
        this(pl, owner, owner.getWorld().spawnEntity(owner.getLocation(), entityType), vector);
    }

    public EffectThrowableEntity(TowerDefensePlugin pl, LivingEntity owner, Entity entity, Vector vector) {
        this.pl = pl;
        this.owner = owner;

        this.entity = entity;
        this.entity.setInvulnerable(true);
        this.entity.setVelocity(vector);
        owner.getWorld().playSound(owner.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);

        preStart();
        run();
    }

    @Override
    public void kill() {
        isPickedUp = true;
        if(entity == null || entity.isDead()){
            return;
        }
        if(Bukkit.getServer().isPrimaryThread()){
            entity.remove();
        }else{
            destroy();
        }
    }

    public void preStart() { }

    private void run() {
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(() -> {
            if(entity == null || entity.isDead()){
                return;
            }
            if(entity.isOnGround()){
               this.destroy();
            }else{
                if(entityMovement != null){
                    entityMovement.move(owner.getEyeLocation().add(0, 0.5, 0));
                }
                ThreadGlobal.runSync(pl, () -> entity.setFallDistance(entity.getFallDistance()/2));
            }

            if(!isPickedUp){
                run();
            }
        });
    }

    public void destroy(){
        isPickedUp = true;
        ThreadGlobal.runSync(pl, () -> entity.remove());
    }

    public void setEntityMovement(AbstractEntityMovement entityMovement) {
        this.entityMovement = entityMovement;
    }
}
