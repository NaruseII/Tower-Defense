package fr.naruse.towerdefense.config;

import fr.naruse.servermanager.core.config.Configuration;

public class ConfigData {

    private static int waitTimer;
    private static int nightTimer;
    private static int dayTimer;

    public static void load(Configuration configuration) {
        Configuration.ConfigurationSection timerSection = configuration.getSection("timer");
        waitTimer = timerSection.getInt("wait");
        nightTimer = timerSection.getInt("night");
        dayTimer = timerSection.getInt("day");
    }

    public static int getWaitTimer() {
        return waitTimer*20;
    }

    public static int getDayTimer() {
        return dayTimer*20;
    }

    public static int getNightTimer() {
        return nightTimer*20;
    }
}
