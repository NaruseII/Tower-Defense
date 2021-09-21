package fr.naruse.towerdefense.unit;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.EffectFloatingItem;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.effect.movement.TargetLocationMovement;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.RunnerPerSecond;
import fr.naruse.towerdefense.utils.world.ITDResource;
import fr.naruse.towerdefense.utils.world.TDRock;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.util.Optional;
import java.util.Set;

public class UnitResourceHarvester extends AbstractUnit {

    public UnitResourceHarvester(TowerDefensePlugin pl) {
        super(pl);

        this.levels.registerNewLevel(Levels.Builder.init(1, 0, 5, 5)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 100)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 1)
                .registerAttribute(LevelAttribute.HARVESTER_HARVEST_LINES, 1)
                .registerAttribute(LevelAttribute.HARVESTER_DISTANCE_FROM_RESOURCE, 10)
                .registerAttribute(LevelAttribute.HARVESTER_HARVESTED_PER_SECOND, 2)
                .registerAttribute(LevelAttribute.HARVESTER_ITEM_SPEED, 6d)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.POLISHED_DEEPSLATE, 0)
                        .addSquare(Material.POLISHED_DEEPSLATE, 2)
                        .addBlock(Material.POLISHED_DEEPSLATE, -1, 1, 1)
                        .addBlock(Material.POLISHED_DEEPSLATE, 1, 1, 1)
                        .addBlock(Material.POLISHED_DEEPSLATE, -1, 1, -1)
                        .addBlock(Material.POLISHED_DEEPSLATE, 1, 1, -1)

                        .addBlock(Material.SHROOMLIGHT, 0, 1, 0)

                        .addBlock(Material.ANVIL, 0, 1, 1)
                        .addBlock(Material.ANVIL, 1, 1, 0)
                        .addBlock(Material.ANVIL, 0, 1, -1)
                        .addBlock(Material.ANVIL, -1, 1, 0)
                        .build())
                        .setRunnableOnUpgrade(() -> this.createTreeOrRockHarvester())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(2, 100, 25, 25)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 200)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 2)
                .registerAttribute(LevelAttribute.HARVESTER_HARVEST_LINES, 1)
                .registerAttribute(LevelAttribute.HARVESTER_DISTANCE_FROM_RESOURCE, 10)
                .registerAttribute(LevelAttribute.HARVESTER_HARVESTED_PER_SECOND, 4)
                .registerAttribute(LevelAttribute.HARVESTER_ITEM_SPEED, 5d)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.CHISELED_POLISHED_BLACKSTONE, 0)
                        .addSquare(Material.CHISELED_POLISHED_BLACKSTONE, 2)
                        .addBlock(Material.CHISELED_POLISHED_BLACKSTONE, -1, 1, 1)
                        .addBlock(Material.CHISELED_POLISHED_BLACKSTONE, 1, 1, 1)
                        .addBlock(Material.CHISELED_POLISHED_BLACKSTONE, -1, 1, -1)
                        .addBlock(Material.CHISELED_POLISHED_BLACKSTONE, 1, 1, -1)

                        .addBlock(Material.SHROOMLIGHT, 0, 1, 0)

                        .addBlock(Material.ANVIL, 0, 1, 1)
                        .addBlock(Material.ANVIL, 1, 1, 0)
                        .addBlock(Material.ANVIL, 0, 1, -1)
                        .addBlock(Material.ANVIL, -1, 1, 0)
                        .build())
                .setRunnableOnUpgrade(() -> this.createTreeOrRockHarvester())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(3, 400, 40, 40)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 300)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 3)
                .registerAttribute(LevelAttribute.HARVESTER_HARVEST_LINES, 1)
                .registerAttribute(LevelAttribute.HARVESTER_DISTANCE_FROM_RESOURCE, 10)
                .registerAttribute(LevelAttribute.HARVESTER_HARVESTED_PER_SECOND, 6)
                .registerAttribute(LevelAttribute.HARVESTER_ITEM_SPEED, 4d)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.NETHERITE_BLOCK, 0)
                        .addSquare(Material.NETHERITE_BLOCK, 2)
                        .addBlock(Material.NETHERITE_BLOCK, -1, 1, 1)
                        .addBlock(Material.NETHERITE_BLOCK, 1, 1, 1)
                        .addBlock(Material.NETHERITE_BLOCK, -1, 1, -1)
                        .addBlock(Material.NETHERITE_BLOCK, 1, 1, -1)

                        .addBlock(Material.SHROOMLIGHT, 0, 1, 0)

                        .addBlock(Material.ANVIL, 0, 1, 1)
                        .addBlock(Material.ANVIL, 1, 1, 0)
                        .addBlock(Material.ANVIL, 0, 1, -1)
                        .addBlock(Material.ANVIL, -1, 1, 0)
                        .build())
                .setRunnableOnUpgrade(() -> this.createTreeOrRockHarvester())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(4, 800, 75, 75)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 400)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 4)
                .registerAttribute(LevelAttribute.HARVESTER_HARVEST_LINES, 1)
                .registerAttribute(LevelAttribute.HARVESTER_DISTANCE_FROM_RESOURCE, 10)
                .registerAttribute(LevelAttribute.HARVESTER_HARVESTED_PER_SECOND, 8)
                .registerAttribute(LevelAttribute.HARVESTER_ITEM_SPEED, 3d)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.OBSIDIAN, 0)
                        .addSquare(Material.OBSIDIAN, 2)
                        .addBlock(Material.OBSIDIAN, -1, 1, 1)
                        .addBlock(Material.OBSIDIAN, 1, 1, 1)
                        .addBlock(Material.OBSIDIAN, -1, 1, -1)
                        .addBlock(Material.OBSIDIAN, 1, 1, -1)

                        .addBlock(Material.SHROOMLIGHT, 0, 1, 0)

                        .addBlock(Material.ANVIL, 0, 1, 1)
                        .addBlock(Material.ANVIL, 1, 1, 0)
                        .addBlock(Material.ANVIL, 0, 1, -1)
                        .addBlock(Material.ANVIL, -1, 1, 0)
                        .build())
                .setRunnableOnUpgrade(() -> this.createTreeOrRockHarvester())
                .build());
    }

    @Override
    void onPlace(Player p, Block block, ItemStack item) {
        this.createTreeOrRockHarvester();
    }

    @Override
    public ItemStack getItemStack() {
        return this.buildItem(Material.POLISHED_DEEPSLATE, "§a§lResource Harverster Unit", false);
    }

    @Override
    public AbstractUnit newInstance() {
        return new UnitResourceHarvester(this.pl);
    }

    private int getHarvestLines(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.HARVESTER_HARVEST_LINES);
    }

    private int getDistanceFromResource(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.HARVESTER_DISTANCE_FROM_RESOURCE);
    }

    private int getHarvestedPerSecond(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.HARVESTER_HARVESTED_PER_SECOND);
    }

    private double getItemSpeed(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.HARVESTER_ITEM_SPEED);
    }

    private void createTreeOrRockHarvester(){
        this.removeEffect(Harvester.class);

        Optional<ITDResource> optional = WorldUtils.getResourcesSet().stream().sorted((o1, o2) -> {
            if(o1.getLocation().distanceSquared(location) >= o2.getLocation().distanceSquared(location)){
                return 1;
            }
            return -1;
        }).findFirst();

        if(optional.isEmpty()){
            return;
        }

        ITDResource resource = optional.get();
        if(TDUtils.distanceSquared(resource.getLocation(), location) > NumberConversions.square(getDistanceFromResource())){
            return;
        }

        for (int i = 0; i < getHarvestLines(); i++) {
            this.registerEffect(new Harvester(resource));
        }
    }

    private class Harvester extends RunnerPerSecond implements IEffect {

        private final ITDResource resource;
        private final Location startLocation;
        private int harvested = 0;

        public Harvester(ITDResource resource) {
            this.resource = resource;
            this.startLocation = resource.getLocation().clone().add(0, 1, 0).toCenterLocation();

            this.start();
        }

        @Override
        public void runPerSecond() {
            this.harvested = 0;
        }

        @Override
        public void run() {
            super.run();

            EffectFloatingItem effectFloatingItem = new EffectFloatingItem(pl, startLocation.clone(), startLocation.clone(), new ItemStack(resource.getMaterial()), null){

                private Location lastLocation;
                @Override
                public void onRun() {
                    super.onRun();

                    if(lastLocation != null && lastLocation.equals(getEntity().getLocation())){
                        kill();
                    }
                    lastLocation = getEntity().getLocation();
                }

                @Override
                public void preStart() {
                    this.setEntityMovement(new TargetLocationMovement(pl, getEntity(), UnitResourceHarvester.this.location.clone().add(0, 1, 0), getItemSpeed()) {
                        @Override
                        public void onHit(Location target) {
                            setCancelled(true);
                        }
                    });
                }
            };

            registerEffect(effectFloatingItem);


            if (this.harvested < getHarvestedPerSecond()) {
                this.harvested++;

                if(resource instanceof TDRock){
                    ResourcesStorage.addStone(1);
                }else{
                    ResourcesStorage.addWood(1);
                }
            }
        }

        @Override
        public void kill() {
            this.setCancelled(true);
        }
    }
}
