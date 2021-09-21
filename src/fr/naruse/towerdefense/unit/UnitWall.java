package fr.naruse.towerdefense.unit;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnitWall extends AbstractUnit {

    public UnitWall(TowerDefensePlugin pl) {
        super(pl);

        this.levels.registerNewLevel(Levels.Builder.init(1, 0, 5, 5)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 25)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 1)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.SPRUCE_PLANKS, 0, 0, 0)
                        .addBlock(Material.SPRUCE_PLANKS, 0, 1, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(2, 50, 25, 25)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 50)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 2)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.CRACKED_STONE_BRICKS, 0, 0, 0)
                        .addBlock(Material.CRACKED_STONE_BRICKS, 0, 1, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(3, 200, 40, 40)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 75)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 3)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.STONE_BRICKS, 0, 0, 0)
                        .addBlock(Material.STONE_BRICKS, 0, 1, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(4, 400, 60, 60)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 100)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 4)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.BRICKS, 0, 0, 0)
                        .addBlock(Material.BRICKS, 0, 1, 0)
                        .build())
                .build());
    }

    @Override
    void onPlace(Player p, Block block, ItemStack item) {}

    @Override
    public ItemStack getItemStack() {
        return this.buildItem(Material.SPRUCE_PLANKS, "§7§lWall Unit", false);
    }

    @Override
    public AbstractUnit newInstance() {
        return new UnitWall(this.pl);
    }

}
