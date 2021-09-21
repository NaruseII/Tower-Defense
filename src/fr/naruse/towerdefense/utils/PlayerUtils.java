package fr.naruse.towerdefense.utils;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;

public class PlayerUtils {

    private static TowerDefensePlugin pl;

    public static void setPlugin(TowerDefensePlugin plugin) {
        pl = plugin;
    }

    public static void sendTitle(String title){
        sendTitle(title, "");
    }

    public static void sendSubTitle(String subTitle){
        sendTitle("", subTitle);
    }

    public static void sendTitle(String title, String subTitle){
        for (Player player : pl.getPlayerInGame()) {
            player.sendTitle(title, subTitle);
        }
    }

    public static void sendMessage(String msg){
        for (Player player : pl.getPlayerInGame()) {
            player.sendMessage(msg);
        }
    }

    public static void setGameMode(GameMode gameMode){
        for (Player p : pl.getPlayerInGame()) {
            p.setGameMode(gameMode);
        }
    }

    public static void forEach(Consumer<Player> consumer){
        pl.getPlayerInGame().forEach(consumer);
    }

    public static Optional<Player> findFirst(){
        return pl.getPlayerInGame().stream().findFirst();
    }

}
