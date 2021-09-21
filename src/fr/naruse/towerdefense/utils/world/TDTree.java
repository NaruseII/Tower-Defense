package fr.naruse.towerdefense.utils.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TDTree implements ITDResource{

    private final List<Block> treeBlocks = Lists.newArrayList();
    private final Location location;

    public TDTree(Location location) {
        this.location = location;

        this.findTreeBlocks(this.location);
    }

    public boolean contains(Block block){
        return this.treeBlocks.contains(block);
    }

    private void findTreeBlocks(Location location) {
        for (Block block : TDUtils.nearBlocks(location.getBlock())) {
            if(this.treeBlocks.contains(block)){
                continue;
            }
            if(block.getType().name().contains("LOG") || block.getType().name().contains("LEAVES")){
                this.treeBlocks.add(block);
                this.findTreeBlocks(block.getLocation());
            }
        }
    }

    public void digEffect() {
        Set<Block> set = Sets.newHashSet(this.treeBlocks);
        Runnable runnable = () -> {
            ParticleUtils.Buffer buffer = new ParticleUtils.Buffer();

            for (Block block : set) {
                IBlockData data = Blocks.N.getBlockData();
                if(block.getType().name().contains("LEAVES")){
                    data = Blocks.al.getBlockData();
                }

                buffer.buildParticle(block.getLocation(), new ParticleParamBlock(ParticleUtils.ParticleType.BLOCK, data), 1.5f, 1.5f, 1.5f, 5, 1);
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
        List<Block> list = this.treeBlocks.stream().filter(block -> block.getType().name().contains("LOG")).collect(Collectors.toList());
        return list.get(TDUtils.RANDOM.nextInt(list.size())).getLocation().clone();
    }

    @Override
    public Material getMaterial() {
        return Material.SPRUCE_LOG;
    }
}
