package fr.naruse.towerdefense.utils.async;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionManager {

    public static final AntiConcurrentBufferSet<Runnable> INFINITE_SECOND_THREAD_RUNNABLE_SET = new AntiConcurrentBufferSet();
    public static final AntiConcurrentBufferSet<Runnable> SECOND_THREAD_RUNNABLE_SET = new AntiConcurrentBufferSet();
    public static final PoolExecutor POOL_EXECUTOR = new PoolExecutor();

    static {
        INFINITE_SECOND_THREAD_RUNNABLE_SET.add(() -> {
            Location location = Bukkit.getWorlds().get(0).getSpawnLocation();

            Set<Player> set = Bukkit.getOnlinePlayers().stream().filter(new Predicate<Player>() {
                @Override
                public boolean test(Player player) {
                    return (TDUtils.distanceSquared(player.getLocation(), location, TDUtils.Axis.X) + TDUtils.distanceSquared(player.getLocation(), location, TDUtils.Axis.Z)) > NumberConversions.square(50);
                }
            }).collect(Collectors.toSet());

            ThreadGlobal.runSync(() -> {
                for (Player player : set) {
                    player.teleport(WorldUtils.getPlatformLocation());
                    player.sendMessage("§cDo not leave your limits!");
                }
            });
        });
    }

    public static class AntiConcurrentBufferSet<T extends Runnable> implements Iterable<T> {

        private final Set<T> set = Sets.newHashSet();

        public void add(T key){
            ThreadGlobal.getExecutorService().submit(() -> {
                set.add(key);
            });
        }

        public boolean contains(T key){
            return set.contains(key);
        }


        public boolean isEmpty(){
            return set.isEmpty();
        }

        public void clear(){
            set.clear();
        }

        @Override
        public Iterator<T> iterator() {
            return set.iterator();
        }
    }

    public static class PoolExecutor {

        public void submit(Runnable runnable){
            Future future = ThreadGlobal.getPoolExecutor().submit(runnable);
            ThreadGlobal.getPoolExecutor().submit(() -> {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

    }

}
