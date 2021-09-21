package fr.naruse.towerdefense.utils.async;

public abstract class Runner {

    private boolean isCancelled = false;

    public void start(){
        Runnable runnable = () -> {
            if(isCancelled){
                return;
            }

            this.run();
            this.start();
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }

    public abstract void run();

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
