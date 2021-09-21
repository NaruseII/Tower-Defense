package fr.naruse.towerdefense.utils.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Set;

public class TDRock implements ITDResource{

    private static final List<ModelUnit> rocksModel = Lists.newArrayList();

    public static ModelUnit getRandomModelUnit(){
        return rocksModel.get(TDUtils.RANDOM.nextInt(rocksModel.size())).clone();
    }

    static {
        rocksModel.add(ModelUnit.Builder.init()
                .addSquare(Material.STONE, 0)
                .addSquare(Material.STONE, 1)
                .addSquare(Material.STONE, 2)
                .build());

        rocksModel.add(ModelUnit.Builder.init()
                .addSquare(Material.STONE, 0)
                .addBlock(Material.STONE, -2, 0, 0)
                .addBlock(Material.STONE, -2, 0, 3)
                .addBlock(Material.STONE, 2, 0, 1)

                .addSquare(Material.STONE, 1)
                .addSquare(Material.STONE, 2)
                .build());
    }

    private final Location location;
    private final ModelUnit modelUnit;

    public TDRock(Location location, ModelUnit modelUnit) {
        this.location = location;
        this.modelUnit = modelUnit;
    }

    public boolean contains(Block block){
        return this.modelUnit.getLocations().contains(block.getLocation());
    }

    public void digEffect() {
        Set<Location> set = Sets.newHashSet(this.modelUnit.getLocations());
        Runnable runnable = () -> {
            ParticleUtils.Buffer buffer = new ParticleUtils.Buffer();

            for (Location loc : set) {
                buffer.buildParticle(loc, new ParticleParamBlock(ParticleUtils.ParticleType.BLOCK, Blocks.b.getBlockData()), 1.5f, 1.5f, 1.5f, 5, 1);
            }

            buffer.send(new ParticleUtils.ParticleSender(ParticleUtils.SendType.TO_ALL));
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }

    @Override
    public Location getLocation() {
        return this.location.clone();
    }

    @Override
    public Location getRandomLocation() {
        return this.modelUnit.getLocations().get(TDUtils.RANDOM.nextInt(this.modelUnit.getLocations().size())).clone();
    }

    @Override
    public Material getMaterial() {
        return Material.STONE;
    }
}
