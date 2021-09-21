package fr.naruse.towerdefense.timer;

import fr.naruse.towerdefense.board.ScoreboardSign;
import fr.naruse.towerdefense.config.ConfigData;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.PlayerUtils;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Bukkit;

public class WaitingTimer {

    private static int timer;
    public static void start(TowerDefensePlugin pl){
        timer = ConfigData.getWaitTimer();

        new Runner() {
            @Override
            public void run() {
                ScoreboardSign.reload();
                if(Bukkit.getOnlinePlayers().size() == 0 || !WorldUtils.isBaseTerraformDone()){
                    timer = ConfigData.getWaitTimer();
                }else{
                    if(timer <= 0){
                        ThreadGlobal.runSync(() -> pl.start());
                        this.setCancelled(true);
                    }else{
                        if(timer <= 10*20 && timer % 20 == 0){
                            PlayerUtils.sendMessage("§7Starting in "+timer/20+" seconds.");
                        }
                        timer--;
                    }
                }
            }
        }.start();
    }

    public static int getTime(){
        return timer/20;
    }

}
