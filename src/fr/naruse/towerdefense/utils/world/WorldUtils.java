package fr.naruse.towerdefense.utils.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldUtils {

    private static final List<Block> spawnableBlocksList = Lists.newArrayList();
    private static final Set<TDTree> treeSet = Sets.newHashSet();
    private static final Set<TDRock> rockSet = Sets.newHashSet();

    private static Location platformLocation;
    private static Location mainLocation;
    private static ArrayList<Block> glassPlatform;
    private static int baseTerraformDoneCount = -1;

    public static void terraformWorldBase(TowerDefensePlugin pl, World world){
        Location location = world.getSpawnLocation();
        mainLocation = location.clone().add(0, -1, 0);
        platformLocation = location.clone().add(0, 30, 0);

        Runnable runnable = () -> {
            pl.getLogger().info("Starting calculus and terraform...");

            Runnable whenDone = () -> {
                baseTerraformDoneCount++;
                if(baseTerraformDoneCount >= 3){
                    generateTreesAndRocks(world);

                    // End of terraform
                    ThreadGlobal.runSync(() -> {

                        location.getBlock().getRelative(0, -1, 0).setType(Material.WARPED_PLANKS);
                        for (Entity entity : world.getEntities()) {
                            if(entity.getType() != EntityType.PLAYER){
                                entity.remove();
                            }
                        }
                    });
                }
            };

            glassPlatform = (ArrayList<Block>) TDUtils.get3DEmptyRectangle(platformLocation, 8, 8, 8);
            platformLocation.add(0, 1, 0);

            baseTerraformDoneCount++;

            new TaskCachedReplaceBlocksBy((List<Block>) glassPlatform.clone(), Material.GLASS, 30, whenDone);
            new TaskReplaceCylinderBlocksBy(Material.AIR, 30, location, 50, 80, true, () -> {
                whenDone.run();
                new TaskReplaceCylinderBlocksBy(Material.GRASS_BLOCK, 30, location.clone().add(0, -1, 0), 50, 0, false, whenDone, true);
            });
        };
        CollectionManager.POOL_EXECUTOR.submit(runnable);
    }

    private static void generateTreesAndRocks(World world) {
        int treeCount = TDUtils.RANDOM.nextInt(3)+2;
        int rocksCount = TDUtils.RANDOM.nextInt(3)+2;

        List<Block> list = TDUtils.get2DCircleBlock(mainLocation.clone(), 45);

        ThreadGlobal.runSync(() -> {
            for (int i = 0; i < treeCount; i++) {
                Location location = list.get(TDUtils.RANDOM.nextInt(list.size())).getLocation();

                Location finalLocation = location.clone().add(0, 1, 0);
                if(world.generateTree(finalLocation, TreeType.BIG_TREE)){
                    treeSet.add(new TDTree(finalLocation));
                }else{
                    i--;
                }

                list.remove(location.getBlock());
            }

            for (int i = 0; i < rocksCount; i++) {
                Location location = list.get(TDUtils.RANDOM.nextInt(list.size())).getLocation();
                Location finalLocation = location.clone().add(0, 1, 0);

                ModelUnit modelUnit = TDRock.getRandomModelUnit();
                if(modelUnit.place(finalLocation)){
                    rockSet.add(new TDRock(finalLocation, modelUnit));
                }else{
                    i--;
                }

                list.remove(location.getBlock());
            }
        });
    }

    public static void deleteGlassPlatform(){
        Runnable runnable = () -> new TaskCachedReplaceBlocksBy(glassPlatform, Material.AIR, 1);
        CollectionManager.POOL_EXECUTOR.submit(runnable);
    }

    public static TDTree findTreeByBlock(Block block){
        for (TDTree tree : treeSet) {
            if(tree.contains(block)){
                return tree;
            }
        }
        return null;
    }

    public static TDRock findRockByBlock(Block block){
        for (TDRock rock : rockSet) {
            if(rock.contains(block)){
                return rock;
            }
        }
        return null;
    }

    public static void night(){
        new Runner() {
            private int plus = 1;
            private boolean b = false;

            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                long time = world.getTime();

                if(time < 14000 || b){
                    ThreadGlobal.runSync(() -> {
                        world.setTime(time + plus);

                        if(b){
                            plus -= 2;
                            if(plus <= 0){
                                setCancelled(true);
                            }
                        }else if(plus <= 100){
                            plus += 2;
                        }

                    });
                }else{
                    b = true;
                }
            }
        }.start();
    }

    public static void day(){
        new Runner() {
            private int plus = 1;
            private boolean b = false;

            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                long time = world.getTime();

                if(time > 2000 || b){
                    ThreadGlobal.runSync(() -> {
                        world.setTime(time + plus);

                        if(b){
                            plus -= 2;
                            if(plus <= 0){
                                setCancelled(true);
                            }
                        }else if(plus <= 100){
                            plus += 2;
                        }

                    });
                }else{
                    b = true;
                }
            }
        }.start();
    }

    public static Location getPlatformLocation() {
        return platformLocation;
    }

    public static Location getMainLocation() {
        return mainLocation;
    }

    public static boolean isBaseTerraformDone(){
        return baseTerraformDoneCount >= 3;
    }

    public static boolean isCalculating(){
        return baseTerraformDoneCount == -1;
    }

    public static Location getRandomSpawnableLocation(){
        return spawnableBlocksList.get(TDUtils.RANDOM.nextInt(spawnableBlocksList.size())).getLocation().add(0, 50, 0);
    }

    public static Set<TDRock> getRockSet() {
        return rockSet;
    }

    public static Set<TDTree> getTreeSet() {
        return treeSet;
    }

    public static Set<ITDResource> getResourcesSet(){
        Set<ITDResource> set = Sets.newHashSet(getRockSet());
        set.addAll(getTreeSet());
        return set;
    }

    private static class TaskReplaceCylinderBlocksBy {

        private final Material toReplace;
        private final int speed;
        private final Location center;
        private final int radius;
        private final int height;
        private final boolean avoidAir;
        private Runnable whenDone;
        private boolean changeBorder = false;

        public TaskReplaceCylinderBlocksBy(Material toReplace, int speed, Location center, int radius, int height, boolean avoidAir, Runnable whenDone, boolean changeBorders) {
            this(toReplace, speed, center, radius, height, avoidAir, whenDone);
            this.changeBorder = changeBorders;
        }

        public TaskReplaceCylinderBlocksBy(Material toReplace, int speed, Location center, int radius, int height, boolean avoidAir, Runnable whenDone) {
            this(toReplace, speed, center, radius, height, avoidAir);
            this.whenDone = whenDone;
        }

        public TaskReplaceCylinderBlocksBy(Material toReplace, int speed, Location center, int radius, int height, boolean avoidAir) {
            this.toReplace = toReplace;
            this.speed = speed;
            this.center = center;
            this.radius = radius;
            this.height = height;
            this.avoidAir = avoidAir;

            run();
        }

        private void run(){
            Runnable runnable = () -> {

                Set<Block> set = Sets.newHashSet();
                Set<Block> changeType = Sets.newHashSet();

                TDUtils.get3DCylinder(center, radius, height, avoidAir, block -> {
                    if(glassPlatform.contains(block)){
                        return;
                    }

                    if(changeBorder){
                        double distance = TDUtils.distanceSquared(center, block.getLocation());
                        if(distance >= NumberConversions.square(45) && distance <= NumberConversions.square(50)){
                            changeType.add(block);
                        }
                    }

                    set.add(block);
                    this.editBlocks(set, changeType, false);
                });

                this.editBlocks(set, changeType, true);

                if(whenDone != null){
                    whenDone.run();
                }
            };
            CollectionManager.POOL_EXECUTOR.submit(runnable);
        }

        private void editBlocks(Set<Block> set, Set<Block> changeType, boolean force){
            if(set.size() >= speed || force){
                Set<Block> finalSet = Sets.newHashSet(set);
                Set<Block> finalChangeType = Sets.newHashSet(changeType);

                set.clear();
                changeType.clear();

                ThreadGlobal.runSync(() -> {
                    for (Block block : finalSet) {
                        if(block.getRelative(0, 1, 0).getType() == Material.WATER){
                            new TaskReplaceNearWater(block.getRelative(0, 1, 0));
                        }
                        if (finalChangeType.contains(block)) {
                            block.setType(Material.CRIMSON_NYLIUM);
                            spawnableBlocksList.add(block);
                        }else{
                            if(block.getType() != toReplace){
                                block.setType(toReplace);
                            }
                        }
                    }
                });

                ThreadGlobal.sleep(50);
            }
        }

    }
}
