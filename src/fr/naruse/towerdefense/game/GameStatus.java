package fr.naruse.towerdefense.game;

public enum GameStatus {

    WAIT,
    GAME,;

    private static GameStatus currentStatus = WAIT;

    public static GameStatus getCurrentStatus() {
        return currentStatus;
    }

    public static boolean isCurrentStatus(GameStatus status){
        return currentStatus == status;
    }

    public void apply(){
        currentStatus = this;
    }

    public boolean isCurrentStatus(){
        return this == currentStatus;
    }
}
