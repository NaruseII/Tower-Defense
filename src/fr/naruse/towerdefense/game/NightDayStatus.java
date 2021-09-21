package fr.naruse.towerdefense.game;

public enum NightDayStatus {

    NIGHT,
    DAY;

    private static NightDayStatus currentStatus = DAY;

    public static NightDayStatus getCurrentStatus() {
        return currentStatus;
    }

    public static boolean isCurrentStatus(NightDayStatus status){
        return currentStatus == status;
    }

    public void apply(){
        currentStatus = this;
    }

    public boolean isCurrentStatus(){
        return this == currentStatus;
    }
}
