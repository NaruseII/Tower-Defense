package fr.naruse.towerdefense.utils.async;

import com.google.common.collect.Lists;

import java.util.List;

public class AsyncList<T> {

    private final List<T> syncList = Lists.newArrayList();
    private final List<T> list = Lists.newArrayList();
    private boolean locked = false;

    public void add(T object){
        ThreadGlobal.runSync(() -> syncList.add(object));
        Runnable runnable = () -> {
            if(locked){
                this.add(object);
                return;
            }
            this.list.add(object);
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }

    public void remove(T object){
        ThreadGlobal.runSync(() -> syncList.remove(object));
        Runnable runnable = () -> {
            if(locked){
                this.remove(object);
                return;
            }
            this.list.remove(object);
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public List<T> getList() {
        return list;
    }

    public List<T> getSyncList() {
        return syncList;
    }
}
