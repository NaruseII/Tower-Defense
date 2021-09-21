package fr.naruse.towerdefense.effect.particle;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.TDUtils;
import net.minecraft.core.particles.ParticleParam;
import org.bukkit.Location;

import java.util.List;
import java.util.Set;

public class ParticleCircleAroundLocationEffect implements IEffect {

    private final int circleAmount;
    private final int circleRadius;
    private final int speed;
    private final ParticleParam[] particleParams;
    private boolean reverse = false;

    private final Set<FollowingParticlePath> loopingParticles = Sets.newHashSet();
    private boolean isCancelled = false;
    private Location location;

    public ParticleCircleAroundLocationEffect(Location location, int circleAmount, int circleRadius, int speed, boolean reverse, ParticleParam... particleParams) {
        this.location = location;
        this.circleAmount = circleAmount;
        this.circleRadius = circleRadius;
        this.speed = speed;
        this.particleParams = particleParams;

        this.reverse = reverse;

        this.init();
        this.play();
    }

    public ParticleCircleAroundLocationEffect(Location location, int circleAmount, int circleRadius, int speed, ParticleParam... particleParams) {
        this.location = location;
        this.circleAmount = circleAmount;
        this.circleRadius = circleRadius;
        this.speed = speed;
        this.particleParams = particleParams;

        this.init();
        this.play();
    }

    private void init() {
        List<Location> circle = TDUtils.getCircle(location, circleRadius, circleAmount);
        ParticleUtils.ParticleSender sender = ParticleUtils.ParticleSender.buildToAll();

        for (int i = 0; i < circle.size(); i++) {

            Location loc = circle.get(i);

            Set<FollowingParticle> set = Sets.newHashSet();
            for (ParticleParam particleParam : particleParams) {
                set.add(new FollowingParticle(loc.clone(), particleParam, sender, location.clone(), speed).setStopOnTouchTarget(false).start());
            }

            this.loopingParticles.add(new FollowingParticlePath(circle, set.toArray(new FollowingParticle[0]), i, reverse).start());
        }
    }

    @Override
    public void kill() {
        isCancelled = true;
        for (FollowingParticlePath loopingParticle : loopingParticles) {
            loopingParticle.setCancelled(true);
        }
    }

    private void play() {
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(() -> {
            if (isCancelled) {
                kill();
                return;
            }

            play();
        });
    }
}