package fr.naruse.towerdefense.unit;

import org.bukkit.scheduler.BukkitRunnable;

public class UnitRunner extends BukkitRunnable {

    @Override
    public void run() {
        for (AbstractUnit unit : AbstractUnit.getSpawnedUnitsList()) {
            if(System.currentTimeMillis()-unit.getLastAttackTime() >= 10000){
                unit.heal(unit.getHealFactor());
            }
        }
    }
}
