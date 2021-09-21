package fr.naruse.towerdefense.effect.particle;


import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleParam;
import org.bukkit.Location;
import org.bukkit.Material;

public class ParticleGrowingCircle {

    private final Location center;
    private final double radiusAdd;
    private final double radiusEnd;
    private final int amountAdd;
    private final int particleCount;
    private final boolean closeToBlock;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final ParticleParam[] particleParam;

    private double d = 0;
    private int amount = 4;

    public ParticleGrowingCircle(Location center, double radiusAdd, double radiusEnd, int amountAdd, int particleCount, boolean closeToBlock, ParticleParam... particleParam) {
        this(center, radiusAdd, radiusEnd, amountAdd, particleCount, closeToBlock, 0, 0, 0, particleParam);
    }

    public ParticleGrowingCircle(Location center, double radiusAdd, double radiusEnd, int amountAdd, int particleCount, boolean closeToBlock, float offsetX, float offsetY, float offsetZ, ParticleParam... particleParam) {
        this.center = center;
        this.radiusAdd = radiusAdd;
        this.radiusEnd = radiusEnd;
        this.amountAdd = amountAdd;
        this.particleCount = particleCount;
        this.closeToBlock = closeToBlock;
        this.particleParam = particleParam;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;

        this.playCircleEffect();
    }

    private void playCircleEffect() {
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(() -> {
            if(d > radiusEnd){
                return;
            }
            for (Location location : TDUtils.getCircle(center, d, amount)) {

                if(closeToBlock){
                    location.add(0, 5, 0);
                    while (location.getBlock().getType() == Material.AIR){
                        if(location.getBlock().getRelative(0, -1, 0).getType() == Material.AIR){
                            location.add(0, -1, 0);
                        }else{
                            break;
                        }
                    }
                }

                for (ParticleParam param : particleParam) {
                    ParticleUtils.buildParticle(location, param, offsetY, offsetX, offsetZ, particleCount, 0).toNearbyFifty();
                }
            }
            d += radiusAdd;
            amount += amountAdd;
            playCircleEffect();
        });
    }
}
