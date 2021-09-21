package fr.naruse.towerdefense.effect.particle;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class ParticleCircleAroundEntityEffect implements IEffect {

    private final Entity entity;
    private final Set<Player> players;
    private final int circleAmount;
    private final int circleRadius;
    private final int speed;

    private final Set<FollowingParticlePath> loopingParticles = Sets.newHashSet();
    private boolean isCancelled = false;
    private Location location;

    public ParticleCircleAroundEntityEffect(Entity entity, Set<Player> players, int circleAmount, int circleRadius, int speed) {
        this.entity = entity;
        this.players = players;
        this.circleAmount = circleAmount;
        this.circleRadius = circleRadius;
        this.speed = speed;

        this.init();
        this.play();
    }

    private void init() {
        this.location = entity.getLocation();
        List<Location> circle = TDUtils.getCircle(entity.getLocation().add(0, 3, 0), circleRadius,  circleAmount);
        ParticleUtils.ParticleSender sender = ParticleUtils.ParticleSender.buildToSome(players);

        for (int i = 0; i < circle.size(); i++) {

            Location loc = circle.get(i);

            this.loopingParticles.add(new FollowingParticlePath(circle, new FollowingParticle[] {
                    new FollowingParticle(loc, ParticleUtils.ParticleType.WITCH, sender, entity.getLocation(), speed).setStopOnTouchTarget(false).start()
                    , new FollowingParticle(loc, ParticleUtils.ParticleType.CLOUD, sender, entity.getLocation(), speed).setStopOnTouchTarget(false).start()
            }, i).start());
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
            if(isCancelled || entity == null || entity.isDead()){
                kill();
                return;
            }

            if(location.distanceSquared(entity.getLocation()) != 0){
                for (FollowingParticlePath loopingParticle : loopingParticles) {
                    loopingParticle.setCancelled(true);
                }
                loopingParticles.clear();
                init();
            }

            play();
        });
    }

}
