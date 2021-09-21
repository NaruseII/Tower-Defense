package fr.naruse.towerdefense.utils.async;

import com.google.common.collect.Lists;
import fr.naruse.servermanager.core.ServerManager;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.List;
import java.util.concurrent.*;

public class ThreadGlobal {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static TowerDefensePlugin pl;
    private static boolean isStopping = false;
    private static int counter = 0;

    private static List<List<Runnable>> listList = Lists.newArrayList();

    public static void launch(TowerDefensePlugin plugin) {
        pl = plugin;
        ScheduledFuture future = EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            if(!listList.isEmpty()){
                List<Runnable> list = listList.get(0);

                list.forEach(Runnable::run);

                listList.remove(list);
                return;
            }

            if(counter >= 10){
                counter = 0;

                List<Runnable> list = getAllRunnable();

                if(list != null){
                    while (list.size() > 50){

                        List<Runnable> newList = Lists.newArrayList();
                        for (int i = 50; i < list.size(); i++) {
                            if(newList.size() >= 50){
                                break;
                            }
                            newList.add(list.get(i));
                            list.remove(list.get(i));
                        }

                        listList.add(newList);
                    }

                    list.forEach(Runnable::run);
                }
            }
            counter++;
        }, 5, 5, TimeUnit.MILLISECONDS);

        EXECUTOR.submit(() -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                if(!(e.getCause() instanceof IllegalPluginAccessException) && !(e.getCause() instanceof RejectedExecutionException)){
                    e.printStackTrace();
                }
                if(!isStopping){
                    launch(plugin);
                }
            }
        });
    }


    private static List<Runnable> getAllRunnable(){
        List<Runnable> set = null;

        if(!CollectionManager.SECOND_THREAD_RUNNABLE_SET.isEmpty()){
            set = Lists.newArrayList(CollectionManager.SECOND_THREAD_RUNNABLE_SET);
            CollectionManager.SECOND_THREAD_RUNNABLE_SET.clear();
        }
        if(!CollectionManager.INFINITE_SECOND_THREAD_RUNNABLE_SET.isEmpty()) {
            if (set == null) {
                set = Lists.newArrayList(CollectionManager.INFINITE_SECOND_THREAD_RUNNABLE_SET);
            } else {
                set.addAll(Lists.newArrayList(CollectionManager.INFINITE_SECOND_THREAD_RUNNABLE_SET));
            }
        }

        return set;
    }

    public static ScheduledExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    public static ExecutorService getPoolExecutor() {
        return EXECUTOR;
    }

    public static void shutdown() {
        isStopping = true;
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR.shutdown();
    }

    public static void runSync(Runnable runnable){
        runSync(pl, runnable);
    }

    public static void runSync(TowerDefensePlugin pl, Runnable runnable){
        if(ServerManager.get().isShuttingDowned()){
            return;
        }
        Bukkit.getScheduler().runTask(pl, runnable);
    }

    public static void runSyncLater(Runnable runnable, int tickLater){
        runSyncLater(pl, runnable, tickLater);
    }

    public static void runSyncLater(TowerDefensePlugin pl, Runnable runnable, int tickLater){
        if(ServerManager.get().isShuttingDowned()){
            return;
        }
        Bukkit.getScheduler().runTaskLater(pl, runnable, tickLater);
    }

    public static void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
