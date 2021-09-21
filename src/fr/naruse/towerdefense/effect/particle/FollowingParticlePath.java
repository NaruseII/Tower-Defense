package fr.naruse.towerdefense.effect.particle;

import fr.naruse.towerdefense.utils.async.CollectionManager;
import org.bukkit.Location;

import java.util.List;

public class FollowingParticlePath {

    private List<Location> locations;
    private final FollowingParticle[] particles;
    private int currentIndex;
    private boolean reverse;

    private boolean isCancelled = false;

    public FollowingParticlePath(List<Location> locations, FollowingParticle[] particles, int startIndex, boolean reverse) {
        this(locations, particles, startIndex);
        this.reverse = reverse;
    }

    public FollowingParticlePath(List<Location> locations, FollowingParticle[] particles, int startIndex) {
        this.locations = locations;
        this.currentIndex = startIndex;
        this.particles = particles;
    }

    public FollowingParticlePath start(){
        Runnable runnable = () -> {

            if(isCancelled){
                for (FollowingParticle particle : this.particles) {
                    particle.setDone(true);
                }
                return;
            }

            boolean next = false;
            for (FollowingParticle particle : particles) {
                if(particle.isOnTarget()){
                    next = true;
                }else{
                    next = false;
                }
            }

            this.start();

            if(!next){
                return;
            }

            if(this.reverse){
                this.currentIndex--;
                if(this.currentIndex < 0){
                    this.currentIndex = locations.size()-1;
                }
            }else{
                this.currentIndex++;
                if(this.currentIndex >= locations.size()){
                    this.currentIndex = 0;
                }
            }

            for (FollowingParticle particle : this.particles) {
                particle.setLocationTarget(locations.get(this.currentIndex));
            }
        };
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
        return this;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}