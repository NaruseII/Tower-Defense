package fr.naruse.towerdefense.board;

import com.google.common.collect.Maps;
import fr.naruse.towerdefense.game.GameStatus;
import fr.naruse.towerdefense.game.NightDayStatus;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.timer.GameTimer;
import fr.naruse.towerdefense.timer.WaitingTimer;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import fr.naruse.towerdefense.wave.Waves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

public class ScoreboardSign {

    private static final Map<Integer, Score> scoreByIndex = Maps.newHashMap();

    private static Scoreboard scoreboard;
    private static Objective objective;

    public static void load(TowerDefensePlugin pl){
        scoreboard = pl.getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("dummy", "dummy", "§2§lTower§4§lDefense");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public static void apply(Player p){
        p.setScoreboard(scoreboard);
    }

    public static void reload(){
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> reload());
            return;
        }

        setScore("§0", 0);
        if (GameStatus.isCurrentStatus(GameStatus.WAIT)) {
            setScore("§7Starting in §f"+ WaitingTimer.getTime() +"§7 seconds", 1);
        }else{
            if(NightDayStatus.isCurrentStatus(NightDayStatus.DAY)){
                setScore("§7Next night: §f"+ GameTimer.getTime() +"§7s", 1);
            }else{
                setScore("§7Next day: §f"+ GameTimer.getTime() +"§7s", 1);
            }
        }
        setScore("§1", 2);
        setScore("§8=============", 3);
        setScore("§2", 4);
        setScore("§7Gold: §6"+ ResourcesStorage.getGold(), 5);
        setScore("§7Wood: §b"+ ResourcesStorage.getWood(), 6);
        setScore("§7Stone: §3"+ ResourcesStorage.getStone(), 7);
        setScore("§7Wave: §c"+ Waves.getCurrentWave(), 8);
        setScore("§3", 9);
    }

    private static void setScore(String line, int index){
        Score score;
        if(scoreByIndex.containsKey(index)){
            score = scoreByIndex.get(index);
            if(score.getEntry().equals(line)){
               return;
            }
            scoreboard.resetScores(score.getEntry());
        }

        score = objective.getScore(line);
        score.setScore(index);
        scoreByIndex.put(index, score);
    }
}
