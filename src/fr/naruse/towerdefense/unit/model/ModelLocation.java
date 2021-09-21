package fr.naruse.towerdefense.unit.model;

public record ModelLocation(int x, int y, int z) {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
