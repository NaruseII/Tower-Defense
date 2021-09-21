package fr.naruse.towerdefense.unit;

import fr.naruse.towerdefense.effect.particle.ParticleCircleAroundLocationEffect;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.Axe;
import fr.naruse.towerdefense.timer.GameTimer;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnitCentral extends AbstractUnit {

    private static UnitCentral instance;

    public static UnitCentral get() {
        return instance;
    }

    public UnitCentral(TowerDefensePlugin pl) {
        super(pl);
        instance = this;

        this.levels.registerNewLevel(Levels.Builder.init(1, 0, 0, 0)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 400)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 1)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, 0, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, 0, 1, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 2, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 3, 0)
                        .build())
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(2, 5000, 25, 25)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 500)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 2)
                .setNewModel(ModelUnit.Builder.init()
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, 0, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, -1)
                        .addBlock(Material.GOLD_BLOCK, 1, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, -1, 0, 1)
                        .addBlock(Material.GOLD_BLOCK, 0, 1, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 2, 0)
                        .addBlock(Material.EMERALD_BLOCK, 0, 3, 0)
                        .build())
                .setRunnableOnUpgrade(() -> this.registerEffect(new ParticleCircleAroundLocationEffect(this.location.clone().toCenterLocation().add(0, 5, 0), 10, 8, 15, ParticleUtils.ParticleType.SOUL)))
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(3, 10000, 100, 100)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 600)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 3)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.EMERALD_BLOCK, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 1, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 2, 0)
                        .addBlock(Material.DIAMOND_BLOCK, 0, 3, 0)
                        .build())
                .setRunnableOnUpgrade(() -> this.registerEffect(new ParticleCircleAroundLocationEffect(this.location.clone().toCenterLocation().add(0, 6, 0), 13, 10, 15, ParticleUtils.ParticleType.CLOUD)))
                .build());

        this.levels.registerNewLevel(Levels.Builder.init(4, 20000, 500, 500)
                .registerAttribute(LevelAttribute.MAX_HEALTH, 600)
                .registerAttribute(LevelAttribute.HEAL_FACTOR, 4)
                .setNewModel(ModelUnit.Builder.init()
                        .addSquare(Material.RAW_GOLD_BLOCK, 0)
                        .addBlock(Material.RAW_GOLD_BLOCK, -1, 1, 1)
                        .addBlock(Material.RAW_GOLD_BLOCK, 1, 1, 1)
                        .addBlock(Material.RAW_GOLD_BLOCK, -1, 1, -1)
                        .addBlock(Material.RAW_GOLD_BLOCK, 1, 1, -1)
                        .addBlock(Material.GOLD_BLOCK, 0, 1, 0)
                        .addBlock(Material.GOLD_BLOCK, 0, 2, 0)
                        .addBlock(Material.DIAMOND_BLOCK, 0, 3, 0)
                        .addBlock(Material.CAMPFIRE, -1, 2, 1)
                        .addBlock(Material.CAMPFIRE, 1, 2, 1)
                        .addBlock(Material.CAMPFIRE, -1, 2, -1)
                        .addBlock(Material.CAMPFIRE, 1, 2, -1)
                        .build())
                .setRunnableOnUpgrade(() -> this.registerEffect(new ParticleCircleAroundLocationEffect(this.location.clone().toCenterLocation().add(0, 7, 0), 15, 11, 15, true, ParticleUtils.ParticleType.TOTEM_OF_UNDYING)))
                .build());
    }

    @Override
    void onPlace(Player p, Block block, ItemStack item) {
        this.registerEffect(new KillableBlock(block));
        this.registerEffect(new ParticleCircleAroundLocationEffect(block.getLocation().toCenterLocation().add(0, 3, 0), 7, 6, 20, ParticleUtils.ParticleType.WITCH));
        this.registerEffect(new ParticleCircleAroundLocationEffect(block.getLocation().toCenterLocation().add(0, 4, 0), 6, 5, 20, ParticleUtils.ParticleType.SOUL_FIRE_FLAME));
        this.registerEffect(new ParticleCircleAroundLocationEffect(block.getLocation().toCenterLocation().add(0, 5, 0), 5, 3, 20, ParticleUtils.ParticleType.FIRE));

        GameTimer.start(pl);

        PlayerUtils.forEach(player -> {
            Axe.WOODEN_AXE.give(p);

            for (int i = 0; i < 8; i++) {
                new UnitSnowballWeapon(pl).giveItem(player);
                new UnitGoldDigger(pl).giveItem(player);
            }

            for (int i = 0; i < 4; i++) {
                new UnitResourceHarvester(pl).giveItem(player);
            }

            for (int i = 0; i < 150; i++) {
                new UnitWall(pl).giveItem(player);
            }

            player.sendMessage("§7Do not forget that §a/shop§7 exists!");
        });

    }

    @Override
    public ItemStack getItemStack() {
        return this.buildItem(Material.GOLD_BLOCK, "§6§lCentral Unit", false);
    }

    @Override
    public AbstractUnit newInstance() {
        return new UnitCentral(this.pl);
    }
}
