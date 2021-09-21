package fr.naruse.towerdefense.storage;

import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Bukkit;

public class ResourcesStorage {

    private static int gold = 0;
    private static int wood = 0;
    private static int stone = 0;

    public static int getGold() {
        return gold;
    }

    public static void addGold(int add){
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> addGold(add));
            return;
        }
        gold += add;
    }

    public static int getWood() {
        return wood;
    }

    public static void addWood(int add){
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> addWood(add));
            return;
        }
        wood += add;
    }

    public static int getStone() {
        return stone;
    }

    public static void addStone(int add){
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> addStone(add));
            return;
        }
        stone += add;
    }
}
