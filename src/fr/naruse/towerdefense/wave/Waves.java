package fr.naruse.towerdefense.wave;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fr.naruse.towerdefense.config.ConfigData;
import fr.naruse.towerdefense.entity.AbstractEntity;
import fr.naruse.towerdefense.entity.TDEntityGolem;
import fr.naruse.towerdefense.entity.TDEntityZombie;
import fr.naruse.towerdefense.entity.TDEntityArmoredZombie;
import fr.naruse.towerdefense.utils.async.RunnerPerSecond;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Waves {

    private static int currentWave = 0;
    private static final Set<Wave> wavesSet = Sets.newHashSet();

    static {
        wavesSet.add(new Wave(0, 2).registerEntity(TDEntityZombie.class, 5));
        wavesSet.add(new Wave(3, 5).registerEntity(TDEntityZombie.class, 23));
        wavesSet.add(new Wave(6, 8).registerEntity(TDEntityZombie.class, 15).registerEntity(TDEntityArmoredZombie.class, 15));
        wavesSet.add(new Wave(9, 10).registerEntity(TDEntityZombie.class, 30).registerEntity(TDEntityArmoredZombie.class, 30));
        wavesSet.add(new Wave(11, 11).registerEntity(TDEntityGolem.class, 2));
    }

    public static void launch(){
        currentWave++;

        for (Wave wave : wavesSet) {
            if(currentWave >= wave.getStartAtWave()  && currentWave <= wave.getEndInWave()){
                wave.startSpawnRunner();
                break;
            }
        }
    }

    public static int getCurrentWave() {
        return currentWave;
    }

    public static void setCurrentWave(int currentWave) {
        Waves.currentWave = currentWave;
    }

    private static class Wave {

        private final Map<Class<? extends AbstractEntity>, Integer> map = Maps.newHashMap();

        private final int startAtWave;
        private final int endInWave;

        private int totalCount = 0;

        public Wave(int startAtWave, int endInWave) {
            this.startAtWave = startAtWave;
            this.endInWave = endInWave;
        }

        public void startSpawnRunner(){
            int perSecond = Math.max(this.totalCount/(ConfigData.getNightTimer()/20), 1);
            final int[] currentNumberSpawn = {0};

            List<Class<? extends AbstractEntity>> list = Lists.newArrayList(this.map.keySet());
            Map<Class<? extends AbstractEntity>, Integer> copyMap = Maps.newHashMap(this.map);

            new RunnerPerSecond() {
                @Override
                public void runPerSecond() {
                    if(currentNumberSpawn[0] >= totalCount){
                        this.setCancelled(true);
                        return;
                    }

                    currentNumberSpawn[0] += perSecond;
                    ThreadGlobal.runSync(() -> {
                        for (int i = 0; i < perSecond; i++) {
                            if(list.isEmpty()){
                                this.setCancelled(true);
                                return;
                            }

                            Class<? extends AbstractEntity> clazz = list.get(0);
                            Integer integer = copyMap.get(clazz);
                            integer--;
                            if(integer == 0){
                                copyMap.remove(clazz);
                                list.remove(clazz);
                            }else{
                                copyMap.put(clazz, integer);
                            }

                            try {
                                clazz.getConstructor(Location.class).newInstance(WorldUtils.getRandomSpawnableLocation());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }.start();
        }

        public Wave registerEntity(Class<? extends AbstractEntity> clazz, int count){
            this.map.put(clazz, count);
            this.totalCount += count;
            return this;
        }

        public int getEndInWave() {
            return endInWave;
        }

        public int getStartAtWave() {
            return startAtWave;
        }
    }
}
