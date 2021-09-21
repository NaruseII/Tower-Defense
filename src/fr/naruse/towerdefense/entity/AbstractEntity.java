package fr.naruse.towerdefense.entity;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.unit.AbstractUnit;
import fr.naruse.towerdefense.utils.async.AsyncList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class AbstractEntity {

    private static final Set<AbstractEntity> abstractEntitySets = Sets.newHashSet();
    private static final AsyncList<Entity> entitySets = new AsyncList();

    public static Set<AbstractEntity> getAbstractEntitySets() {
        return abstractEntitySets;
    }

    public static AsyncList<Entity> getEntitySets() {
        return entitySets;
    }

    private final LivingEntity entity;
    private final int attackInterval;

    private AbstractUnit targetUnit;
    private int currentAttackCount = 0;

    public AbstractEntity(EntityType entityType, Location spawn, int attackInterval) {
        this.attackInterval = attackInterval;
        this.entity = (LivingEntity) spawn.getWorld().spawnEntity(spawn, entityType);
        if(entityType == EntityType.ZOMBIE){
            this.entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        }

        abstractEntitySets.add(this);
        entitySets.add(entity);
    }

    public abstract double getDamage();

    public void onDie(Entity entity){
        if(entity.equals(this.entity)){
            abstractEntitySets.remove(this);
            entitySets.remove(entity);
        }
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public AbstractUnit getTargetUnit() {
        return targetUnit;
    }

    public void setTargetUnit(AbstractUnit targetUnit) {
        this.targetUnit = targetUnit;
    }

    public boolean canAttack() {
        if(this.currentAttackCount >= this.attackInterval){
            this.currentAttackCount = 0;
            return true;
        }
        this.currentAttackCount++;
        return false;
    }
}
