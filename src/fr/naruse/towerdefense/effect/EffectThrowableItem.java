package fr.naruse.towerdefense.effect;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.particle.ParticleCircleAroundEntityEffect;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EffectThrowableItem implements IEffect {

    private final TowerDefensePlugin pl;
    private final Player p;
    private final ArmorStand entity;
    private final boolean showItemLocation;
    private final boolean circleItemEffect;
    private final int distanceBetweenParticle;
    private final int pointAmount;
    private final int radius;
    private final int speed;

    private boolean isPickedUp = false;
    private boolean isOnGround = false;
    private ParticleCircleAroundEntityEffect particleCircleAroundEntityEffect;

    public EffectThrowableItem(TowerDefensePlugin pl, Player p, ItemStack itemStack, Vector vector) {
        this(pl, p, itemStack, vector, false, false, 0, 0, 0, 0);
    }

    public EffectThrowableItem(TowerDefensePlugin pl, Player p, ItemStack itemStack, Vector vector, boolean showItemLocation, int distanceBetweenParticle) {
        this(pl, p, itemStack, vector, showItemLocation, false, distanceBetweenParticle, 0, 0, 0);
    }

    public EffectThrowableItem(TowerDefensePlugin pl, Player p, ItemStack itemStack, Vector vector, boolean circleItemEffect, int pointAmount, int radius, int speed) {
        this(pl, p, itemStack, vector, false, circleItemEffect, 0, pointAmount, radius, speed);
    }

    public EffectThrowableItem(TowerDefensePlugin pl, Player p, ItemStack itemStack, Vector vector, boolean showItemLocation, boolean circleItemEffect, int distanceBetweenParticle, int pointAmount, int radius, int speed) {
        this.pl = pl;
        this.p = p;
        this.showItemLocation = showItemLocation;
        this.circleItemEffect = circleItemEffect;
        this.distanceBetweenParticle = distanceBetweenParticle;
        this.pointAmount = pointAmount;
        this.radius = radius;
        this.speed = speed;

        this.entity = (ArmorStand) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.ARMOR_STAND);
        this.entity.setInvisible(true);
        this.entity.setInvulnerable(true);
        this.entity.setVelocity(vector);
        this.entity.getEquipment().setItemInMainHand(itemStack);
        p.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);

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

    private void run() {
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(() -> {
            if(entity == null || entity.isDead()){
                return;
            }
            if(entity.isOnGround()){
                if(!isOnGround){
                    isOnGround = true;

                    this.onTouchGround();

                    Block block = entity.getLocation().getBlock().getRelative(0, -1, 0);
                    IBlockData iBlockData = ((CraftWorld) entity.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));

                    ParticleUtils.buildParticle(entity.getLocation().add(0, 1, 0), new ParticleParamBlock(ParticleUtils.ParticleType.BLOCK, iBlockData), 1, 0.5f, 1, 15, 1).toNearbyFifty();
                }
                if(particleCircleAroundEntityEffect == null && circleItemEffect){
                    particleCircleAroundEntityEffect = new ParticleCircleAroundEntityEffect(entity, Sets.newHashSet(p), pointAmount,radius, speed);
                }

                if(showItemLocation){
                    for (Location location : TDUtils.getLocationsBetweenTwoPoints(p.getLocation().add(0, 0.3, 0), entity.getLocation().add(0, 1, 0), distanceBetweenParticle)) {
                        if (location.getBlock().getType() != Material.AIR) {
                            continue;
                        }

                        ParticleUtils.buildParticle(location, ParticleUtils.ParticleType.TOTEM_OF_UNDYING, 0, 0, 0, 1, 0).toOne(p);
                    }
                }

                TDUtils.getNearbyPlayers(entity.getLocation(), 1, 1, 1).forEach(player -> {
                    if(isPickedUp){
                        return;
                    }
                    if(pickUp(player)){
                        isPickedUp = true;
                        destroy();
                        ThreadGlobal.runSync(pl, () -> player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1));
                    }
                });
            }else{

                if(particleCircleAroundEntityEffect != null){
                    particleCircleAroundEntityEffect.kill();
                    particleCircleAroundEntityEffect = null;
                }

                ThreadGlobal.runSync(pl, () -> entity.setFallDistance(entity.getFallDistance()/2));

                TDUtils.getNearbyEntities(entity.getLocation(), 1.5, 1.5, 1.5).forEach(entity -> {
                    if(entity != p && entity instanceof LivingEntity && entity != this.entity){
                        this.onHitEntity(entity);
                    }
                });
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

    public boolean pickUp(Player player){
        return false;
    }

    public void onHitEntity(Entity entity) { }

    public void onTouchGround() { }
}
