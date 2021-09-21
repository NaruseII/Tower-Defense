package fr.naruse.towerdefense.utils.world;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Set;

public class TaskCachedReplaceBlocksBy {

    public TaskCachedReplaceBlocksBy(List<Block> list, Material toReplace, int speed, Runnable whenDone){
        run(list, toReplace, speed, whenDone);
    }

    public TaskCachedReplaceBlocksBy(List<Block> list, Material toReplace, int speed){
        run(list, toReplace, speed, null);
    }

    private void run(List<Block> list, Material toReplace, int speed, Runnable whenDone){
        Runnable runnable = () -> {
            while (!list.isEmpty()){
                Set<Block> set = Sets.newHashSet();

                for (int i = 0; i < speed; i++) {
                    if(list.isEmpty()){
                        break;
                    }

                    Block block = list.get(0);
                    list.remove(block);
                    if(block.getType() != toReplace) {
                        set.add(block);
                    }else {
                        i--;
                    }
                }

                ThreadGlobal.runSync(() -> {
                    for (Block block : set) {
                        block.setType(toReplace);
                    }
                });

                ThreadGlobal.sleep(50);
            }

            if(whenDone != null){
                whenDone.run();
            }
        };
        CollectionManager.POOL_EXECUTOR.submit(runnable);
    }

}
