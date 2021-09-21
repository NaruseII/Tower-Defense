package fr.naruse.towerdefense.utils.world;

import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class TaskReplaceNearWater {

    public TaskReplaceNearWater(Block block) {
        run(block);
    }

    private void run(Block mainBlock){
        Runnable runnable = () -> {
            ThreadGlobal.runSync(() -> mainBlock.setType(Material.AIR));
            for (Block block : TDUtils.nearBlocks(mainBlock)) {
                if(block.getType() == Material.WATER){
                    run(block);
                }
            }
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }
}
