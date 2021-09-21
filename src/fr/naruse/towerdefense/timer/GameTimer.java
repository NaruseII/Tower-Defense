package fr.naruse.towerdefense.timer;

import fr.naruse.towerdefense.board.ScoreboardSign;
import fr.naruse.towerdefense.config.ConfigData;
import fr.naruse.towerdefense.game.NightDayStatus;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.utils.PlayerUtils;
import fr.naruse.towerdefense.utils.async.Runner;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import fr.naruse.towerdefense.wave.Waves;

public class GameTimer {

    private static int timer;

    public static void start(TowerDefensePlugin pl){
        timer = ConfigData.getDayTimer();

        new Runner() {
            @Override
            public void run() {
                ScoreboardSign.reload();

                if(NightDayStatus.DAY.isCurrentStatus()){
                    if(timer > 0){
                        timer--;
                    }else{
                        // pass to night
                        NightDayStatus.NIGHT.apply();
                        timer = ConfigData.getNightTimer();
                        WorldUtils.night();
                        PlayerUtils.sendMessage("§aThe night starts!");
                        Waves.launch();
                    }
                }else{
                    if(timer > 0){
                        timer--;
                    }else{
                        // pass to day
                        NightDayStatus.DAY.apply();
                        timer = ConfigData.getDayTimer();
                        WorldUtils.day();
                        PlayerUtils.sendMessage("§aThe day appears!");
                    }
                }
            }
        }.start();
    }

    public static int getTime(){
        return timer/20;
    }

    public static void setTimer(int i) {
        timer = 5*20;
    }
}
