package fr.naruse.towerdefense.entity;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.unit.AbstractUnit;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.Optional;

public class EntityRunner extends BukkitRunnable {

    @Override
    public void run() {
        for (AbstractEntity abstractEntity : Sets.newHashSet(AbstractEntity.getAbstractEntitySets())) {
            LivingEntity livingEntity = abstractEntity.getEntity();

            if(livingEntity.isDead()){
                abstractEntity.onDie(livingEntity);
                continue;
            }

            if(abstractEntity.getTargetUnit() == null){
                Optional<AbstractUnit> optional = AbstractUnit.getSpawnedUnitsList().stream().sorted((o1, o2) -> {
                    if(o1.getLocation().distanceSquared(livingEntity.getLocation()) >= o2.getLocation().distanceSquared(livingEntity.getLocation())){
                        return 1;
                    }
                    return -1;
                }).findFirst();

                if(optional.isPresent()){
                    abstractEntity.setTargetUnit(optional.get());
                }
            }

            if(abstractEntity.getTargetUnit() != null){
                AbstractUnit targetUnit = abstractEntity.getTargetUnit();
                if(targetUnit.isDead()){
                    abstractEntity.setTargetUnit(null);
                    continue;
                }

                this.executeMovement(livingEntity, targetUnit.getLocation(), 1);

                if(livingEntity.getLocation().distanceSquared(targetUnit.getLocation()) <= NumberConversions.square(4) && abstractEntity.canAttack()){
                    targetUnit.hurt(abstractEntity.getDamage());
                }
            }
        }
    }

    private void executeMovement(LivingEntity entity, Location destination, double speed) {
        try{
            EntityLiving craftEntity = ((CraftLivingEntity) entity).getHandle();
            EntityInsentient entityInsentient = (EntityInsentient) craftEntity;
            PathEntity pathEntity = entityInsentient.getNavigation().createPath(destination.getX(), destination.getY(), destination.getZ(), 0);
            if(pathEntity != null){
                entityInsentient.getNavigation().a(pathEntity, speed);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
