package fr.naruse.towerdefense.utils.async;

public abstract class RunnerPerSecond extends Runner{

    public abstract void runPerSecond();

    private int tick = 0;

    @Override
    public void run() {
        if(tick >= 20){
            this.runPerSecond();
            tick = 0;
        }else{
            tick++;
        }
    }

    public int getTick() {
        return tick;
    }
}
