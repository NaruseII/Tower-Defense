package fr.naruse.towerdefense.utils.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Map;

public class TaskCachedReplaceBlockByMultiple {

    public TaskCachedReplaceBlockByMultiple(Map<Location, Material> map, int speed, Runnable whenDone){
        run(map, speed, whenDone);
    }

    public TaskCachedReplaceBlockByMultiple(Map<Location, Material> map, int speed){
        run(map, speed, null);
    }

    private void run(Map<Location, Material> map, int speed, Runnable whenDone){
        Runnable runnable = () -> {
            List<Location> list = Lists.newArrayList(map.keySet());

            while (!list.isEmpty()){
                Map<Block, Material> finalMap = Maps.newHashMap();

                for (int i = 0; i < speed; i++) {
                    if(list.isEmpty()){
                        break;
                    }

                    Location location = list.get(0);
                    list.remove(location);

                    Material material = map.get(location);
                    Block block = location.getBlock();

                    if(block.getType() != material) {
                        finalMap.put(block, material);
                    }else {
                        i--;
                    }
                }

                ThreadGlobal.runSync(() -> {
                    finalMap.forEach((block, material) -> block.setType(material));
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
