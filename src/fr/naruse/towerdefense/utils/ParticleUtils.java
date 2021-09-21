package fr.naruse.towerdefense.utils;

import com.google.common.collect.Sets;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class ParticleUtils {

    public static Particle buildParticle(Location location, ParticleParam particle, float xOffset, float yOffset, float zOffset, int count, float speed){
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, location.getX(), location.getY(), location.getZ(), xOffset, yOffset, zOffset, speed, count);
        return new Particle(packet, location);
    }

    public static Particle buildParticle(Location location, ParticleParam particle, float offsetX, float offsetY, float offsetZ, int amount){
        return buildParticle(location, particle, offsetX, offsetY, offsetZ, amount, 0f);
    }

    public record Particle(PacketPlayOutWorldParticles packet, Location location) {

        public void toAll() {
            Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
        }

        public void toNearbyFifty() {
            this.toNearby(50, 50, 50);
        }

        public void toNearby(int x, int y, int z) {
            TDUtils.getNearbyPlayers(location, x, y, z).forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
        }

        public void toOne(Player player) {
            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
        }

        public void toSome(Player... players) {
            Arrays.stream(players).forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
        }

        public void toSome(Set<Player> playerSet) {
            playerSet.forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
        }

    }

    public record ParticleSender<E>(SendType sendType, Object... objects) {

        public static ParticleSender buildToAll() {
            return new ParticleSender(SendType.TO_ALL);
        }

        public static ParticleSender buildToNearby(int x, int y, int z) {
            return new ParticleSender(SendType.TO_NEARBY, x, y, z);
        }

        public static ParticleSender buildToOne(Player player) {
            return new ParticleSender(SendType.TO_ONE, player);
        }

        public static ParticleSender buildToSome(Set<Player> set) {
            return new ParticleSender(SendType.TO_SOME_SET, set);
        }

        public static ParticleSender buildToSome(Player... players) {
            return new ParticleSender(SendType.TO_SOME_ARRAY, players);
        }

        public void send(Particle particle) {
            switch (sendType) {
                case TO_ALL:
                    particle.toAll();
                    break;
                case TO_ONE:
                    particle.toOne((Player) objects[0]);
                    break;
                case TO_NEARBY:
                    particle.toNearby((int) objects[0], (int) objects[1], (int) objects[2]);
                    break;
                case TO_SOME_SET:
                    particle.toSome((Set<Player>) objects[0]);
                    break;
                case TO_SOME_ARRAY:
                    particle.toSome((Player[]) objects[0]);
                    break;
            }
        }
    }

    public static class Buffer {

        private Set<Particle> particleSet = Sets.newHashSet();

        public void buildParticle(Location location, ParticleParam particle, float xOffset, float yOffset, float zOffset, int count, float speed){
            this.particleSet.add(ParticleUtils.buildParticle(location, particle, xOffset, yOffset, zOffset, count, speed));
        }

        public void buildParticle(Location location, ParticleParam particle, float offsetX, float offsetY, float offsetZ, int amount){
            this.particleSet.add(ParticleUtils.buildParticle(location, particle, offsetX, offsetY, offsetZ, amount));
        }

        public void send(ParticleSender sender){
            for (Particle particle : this.particleSet) {
                sender.send(particle);
            }
        }

    }

    public enum SendType {

        TO_ALL,
        TO_NEARBY,
        TO_ONE,
        TO_SOME_SET,
        TO_SOME_ARRAY

    }

    public static class ParticleType {

        public static final ParticleParam SOUL_FIRE_FLAME = Particles.D;
        public static final ParticleParam FIRE = Particles.C;
        public static final net.minecraft.core.particles.Particle<ParticleParamBlock> BLOCK = Particles.e;
        public static final net.minecraft.core.particles.Particle<ParticleParamRedstone> REDSTONE = Particles.p;
        public static final ParticleParam TOTEM_OF_UNDYING = Particles.aa;
        public static final ParticleParam WITCH = Particles.ad;
        public static final ParticleParam CLOUD = Particles.g;
        public static final ParticleParam HEART = Particles.I;
        public static final ParticleParam SOUL = Particles.E;
    }

}
