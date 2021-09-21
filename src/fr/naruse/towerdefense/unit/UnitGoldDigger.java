package fr.naruse.towerdefense.unit;

import fr.naruse.towerdefense.effect.EffectItemDropper;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.async.RunnerPerSecond;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnitGoldDigger extends AbstractUnit {

    private final int goldPerSecond = 1;

    public UnitGoldDigger(TowerDefensePlugin pl) {
        super(pl);

        this.levels.registerNewLevel(Levels.Builder.init(1, 0, 5, 5)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 100)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 1)
                .registerAttribute(LevelAttribute.GOLD_DIGGER_PER_SECOND, 1)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_ORE, 0, 0, 0)
                        .addBlock(Material.DEEPSLATE_GOLD_ORE, 0, 1, 0)
                        .addBlock(Material.DEEPSLATE_GOLD_ORE, 0, 2, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(2, 100, 25, 25)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 200)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 2)
                .registerAttribute(LevelAttribute.GOLD_DIGGER_PER_SECOND, 3)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 0)
                        .addBlock(Material.DEEPSLATE_GOLD_ORE, 0, 1, 0)
                        .addBlock(Material.DEEPSLATE_GOLD_ORE, 0, 2, 0)
                        .build())
                .setRunnableOnUpgrade(() -> {
                    this.removeEffect(EffectItemDropper.class);
                    this.registerEffect(new EffectItemDropper(location.clone().add(0, 3, 0), Material.GOLD_INGOT, 3));
                })
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(3, 400, 40, 40)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 300)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 3)
                .registerAttribute(LevelAttribute.GOLD_DIGGER_PER_SECOND, 5)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 0)
                        .addBlock(Material.NETHER_GOLD_ORE, 0, 1, 0)
                        .addBlock(Material.DEEPSLATE_GOLD_ORE, 0, 2, 0)
                        .build())
                .setRunnableOnUpgrade(() -> {
                    this.removeEffect(EffectItemDropper.class);
                    this.registerEffect(new EffectItemDropper(location.clone().add(0, 3, 0), Material.GOLD_INGOT, 2));
                })
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(4, 800, 75, 75)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 400)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 4)
                .registerAttribute(LevelAttribute.GOLD_DIGGER_PER_SECOND, 8)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 0)
                        .addBlock(Material.OBSIDIAN, 0, 1, 0)
                        .addBlock(Material.NETHER_GOLD_ORE, 0, 2, 0)
                        .build())
                .setRunnableOnUpgrade(() -> {
                    this.removeEffect(EffectItemDropper.class);
                    this.registerEffect(new EffectItemDropper(location.clone().add(0, 3, 0), Material.GOLD_INGOT, 1));
                })
                .build());
    }

    @Override
    void onPlace(Player p, Block block, ItemStack item) {
        this.registerEffect(new EffectItemDropper(block.getLocation().clone().add(0, 3, 0), Material.GOLD_INGOT, 5));
        this.registerEffect(new Digger());
    }

    @Override
    public ItemStack getItemStack() {
        return this.buildItem(Material.GOLD_ORE, "§e§lGold Digger Unit", false);
    }

    @Override
    public AbstractUnit newInstance() {
        return new UnitGoldDigger(this.pl);
    }

    private int getGoldDigPerSecond(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.GOLD_DIGGER_PER_SECOND);
    }

    private class Digger extends RunnerPerSecond implements IEffect {

        private int goldDig = 0;

        public Digger() {
            this.start();
        }

        @Override
        public void runPerSecond() {
            this.goldDig = 0;
        }

        @Override
        public void run() {
            super.run();
            if(this.goldDig < getGoldDigPerSecond()){
                this.goldDig++;
                ResourcesStorage.addGold(1);
            }
        }

        @Override
        public void kill() {
            this.setCancelled(true);
        }
    }
}
