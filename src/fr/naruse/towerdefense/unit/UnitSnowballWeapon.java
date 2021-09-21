package fr.naruse.towerdefense.unit;

import com.google.common.collect.Lists;
import fr.naruse.towerdefense.effect.EffectFloatingItem;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.effect.movement.FollowingEntityMovement;
import fr.naruse.towerdefense.effect.movement.TargetEntityMovement;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class UnitSnowballWeapon extends AbstractUnit {

    public UnitSnowballWeapon(TowerDefensePlugin pl) {
        super(pl);

        this.levels.registerNewLevel(Levels.Builder.init(1, 0, 5, 5)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 100)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 1)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_AMOUNT, 6)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_DAMAGE, 3d)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_SPEED, 6d)
                .registerAttribute(LevelAttribute.WEAPON_VIEW_RADIUS, 10)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.SPRUCE_WOOD, 0, 0, 0)
                        .addBlock(Material.REDSTONE_ORE, 0, 1, 0)
                        .addBlock(Material.RED_NETHER_BRICK_WALL, 0, 2, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(2, 100, 25, 25)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 200)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 2)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_AMOUNT, 9)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_DAMAGE, 5d)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_SPEED, 5d)
                .registerAttribute(LevelAttribute.WEAPON_VIEW_RADIUS, 15)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.SPRUCE_WOOD, 0, 0, 0)
                        .addBlock(Material.DEEPSLATE_REDSTONE_ORE, 0, 1, 0)
                        .addBlock(Material.RED_NETHER_BRICK_WALL, 0, 2, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(3, 400, 40, 40)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 300)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 3)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_AMOUNT, 12)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_DAMAGE, 7d)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_SPEED, 4d)
                .registerAttribute(LevelAttribute.WEAPON_VIEW_RADIUS, 20)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.STRIPPED_SPRUCE_LOG, 0, 0, 0)
                        .addBlock(Material.REDSTONE_BLOCK, 0, 1, 0)
                        .addBlock(Material.RED_NETHER_BRICK_WALL, 0, 2, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(4, 800, 75, 75)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 400)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 4)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_AMOUNT, 15)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_DAMAGE, 9d)
                .registerAttribute(LevelAttribute.WEAPON_BULLET_SPEED, 3d)
                .registerAttribute(LevelAttribute.WEAPON_VIEW_RADIUS, 25)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.STRIPPED_SPRUCE_LOG, 0, 0, 0)
                        .addBlock(Material.OBSIDIAN, 0, 1, 0)
                        .addBlock(Material.RED_NETHER_BRICK_WALL, 0, 2, 0)
                        .build())
                .build());
    }

    @Override
    void onPlace(Player p, Block block, ItemStack item) {
        this.registerEffect(new Attack(block.getLocation().clone().add(0, 2, 0)));
    }

    @Override
    public ItemStack getItemStack() {
        return this.buildItem(Material.SPRUCE_WOOD, "§b§lSnowball Weapon Unit", false);
    }

    @Override
    public AbstractUnit newInstance() {
        return new UnitSnowballWeapon(this.pl);
    }

    private int getBulletAmount(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.WEAPON_BULLET_AMOUNT);
    }

    private double getBulletDamage(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.WEAPON_BULLET_DAMAGE);
    }

    private double getBulletSpeed(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.WEAPON_BULLET_SPEED);
    }

    private int getViewRadius(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.WEAPON_VIEW_RADIUS);
    }

    private class Attack extends Runner implements IEffect {

        private final List<EffectFloatingItem> list = Lists.newArrayList();

        private final Location location;

        public Attack(Location location) {
            this.location = location;
            this.start();
        }

        @Override
        public void kill() {
            this.setCancelled(true);
            for (EffectFloatingItem effectFloatingItem : this.list) {
                effectFloatingItem.kill();
            }
            this.list.clear();
        }

        @Override
        public void run() {
            for (int i = 0; i < this.list.size(); i++) {
                EffectFloatingItem effect = list.get(i);
                if(effect.isCancelled()){
                    this.list.remove(effect);
                }
            }

            if(this.list.size() < getBulletAmount()){
                this.addArrow(1);
            }
        }

        private void addArrow(int count){
            for (int i = 0; i < count; i++) {
                list.add(new EffectFloatingItem(pl, location.clone().add(0, 1, 0), location.clone().add(0, 1, 0), buildItem(Material.SNOWBALL, TDUtils.RANDOM.nextLong()+"", false), null, FollowingEntityMovement.class){
                    @Override
                    public void onRun() {
                        Optional<Entity> optional = TDUtils.getNearbyEntities(location, getViewRadius(), getViewRadius(), getViewRadius()).filter(entity -> !(entity instanceof Player) && entity instanceof Mob).findFirst();

                        if(optional.isPresent()){

                            this.setEntityMovement(new TargetEntityMovement<>(pl, getEntity(), (LivingEntity) optional.get(), getBulletSpeed()) {

                                private Location lastLocation;
                                private long millis;

                                @Override
                                public void onRun() {
                                    if(getTarget() == null || getTarget().isDead()){
                                        setEntityMovement(new FollowingEntityMovement(pl, getEntity(), 0.5));
                                    }else{
                                        if (lastLocation != null) {
                                            if (getEntity().getLocation().equals(lastLocation)) {
                                                if(System.currentTimeMillis()-millis <= 1000){
                                                    return;
                                                }
                                                millis = System.currentTimeMillis();
                                                ThreadGlobal.runSync(() -> getEntity().teleport(getEntity().getLocation().add(0, 4, 0)));
                                            }
                                        }
                                        lastLocation = getEntity().getLocation();
                                    }
                                }

                                @Override
                                public void onHit(LivingEntity target) {
                                    setCancelled(true);
                                    ThreadGlobal.runSync(() -> target.damage(getBulletDamage()));
                                }
                            });

                        }
                    }
                });
            }
        }
    }
}
