package fr.naruse.towerdefense.utils;

import com.google.common.collect.Lists;
import fr.naruse.towerdefense.entity.AbstractEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class TDUtils {

    public static final Random RANDOM = new Random();

    public static Stream<Player> getNearbyPlayers(Location location, double x, double y, double z){
        return Bukkit.getOnlinePlayers().stream().filter(entity -> distanceSquared(entity.getLocation(), location, Axis.X) <= NumberConversions.square(x)
                && distanceSquared(entity.getLocation(), location, Axis.Y) <= NumberConversions.square(y)
                && distanceSquared(entity.getLocation(), location, Axis.Z) <= NumberConversions.square(z)).map((Function<Player, Player>) player -> player);
    }

    public static Stream<Entity> getNearbyEntities(Location location, double x, double y, double z){
        return AbstractEntity.getEntitySets().getList().stream().filter(entity -> distanceSquared(entity.getLocation(), location, Axis.X) <= NumberConversions.square(x)
                && distanceSquared(entity.getLocation(), location, Axis.Y) <= NumberConversions.square(y)
                && distanceSquared(entity.getLocation(), location, Axis.Z) <= NumberConversions.square(z));
    }

    public static double distanceSquared(Location o, Location b) {
        if (o == null) {
            return Integer.MAX_VALUE;
        } else if (o.getWorld() != null && b.getWorld() != null) {
            if (o.getWorld() != b.getWorld()) {
                return Integer.MAX_VALUE;
            } else {
                return NumberConversions.square(b.getX() - o.getX()) + NumberConversions.square(b.getY() - o.getY()) + NumberConversions.square(b.getZ() - o.getZ());
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static double distanceSquared(Location o, Location b, Axis... axis) {
        double d = 0;
        for (Axis axe : axis) {
            d += distanceSquared(o, b, axe);
        }
        return d;
    }

    public static double distanceSquared(Location o, Location b, Axis axis) {
        if (o == null) {
            return Integer.MAX_VALUE;
        } else if (o.getWorld() != null && b.getWorld() != null) {
            if (o.getWorld() != b.getWorld()) {
                return Integer.MAX_VALUE;
            } else {
                return axis == Axis.X ? NumberConversions.square(b.getX() - o.getX()) : axis == Axis.Y ? NumberConversions.square(b.getY() - o.getY()) : NumberConversions.square(b.getZ() - o.getZ());
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static Vector genVector(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);
        return new Vector(x, z, y);
    }

    public static List<Location> getLocationsBetweenTwoPoints(Location a, Location b, double perBlock){
        a = a.clone();

        List<Location> list = Lists.newArrayList();

        double distance = a.distance(b);
        int count = (int) Math.floor(distance / perBlock);

        double xToAdd = Math.abs(a.getX()-b.getX())/count;
        double yToAdd = Math.abs(a.getY()-b.getY())/count;
        double zToAdd = Math.abs(a.getZ()-b.getZ())/count;

        if(!needToAddPositive(a, b, Axis.X)){
            xToAdd = -xToAdd;
        }
        if(!needToAddPositive(a, b, Axis.Y)){
            yToAdd = -yToAdd;
        }
        if(!needToAddPositive(a, b, Axis.Z)){
            zToAdd = -zToAdd;
        }

        for (int i = 0; i < count; i++) {
            a.add(xToAdd, yToAdd, zToAdd);
            list.add(a.clone());
        }

        return list;
    }

    private static boolean needToAddPositive(Location a, Location b, Axis axis){
        switch (axis){
            case X:
                return !(a.getX()-b.getX() > 0);
            case Y:
                return !(a.getY()-b.getY() > 0);
            default:
                return !(a.getZ()-b.getZ() > 0);
        }
    }

    public static List<Location> getLimitedCircle(Location center, double radius, int amount, int limitTo) {
        return getCircle(center, radius, amount, 0, limitTo);
    }

    public static List<Location> getCircle(Location center, double radius, int amount) {
        return getCircle(center, radius, amount, 0);
    }

    public static List<Location> getCircle(Location center, double radius, int amount, double constantYAdd) {
        return getCircle(center, radius, amount, constantYAdd, -1);
    }

    public static List<Location> getCircle(Location center, double radius, int amount, double constantYAdd, int limitTo) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for(int i = 0;i < amount; i++) {

            if(limitTo != -1 && i > limitTo){
                break;
            }

            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.add(0, constantYAdd, 0).getY(), z));
        }
        return locations;
    }

    public static List<Location> getSphere(Location loc, int radius, int height, boolean hollow, boolean sphere, double add){
        List<Location> circleblocks = new ArrayList();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        for(double x = cx - radius; x <= cx + radius; x+=add){
            for (double z = cz - radius; z <= cz + radius; z+=add){
                for(double y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y+=add){
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);

                    if(dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))){
                        Location l = new Location(loc.getWorld(), x, y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static List<Location> getVerticalCircle(Location center, double radius, int amount) {
        return getVerticalCircle(center, radius, amount, 0);
    }

    public static List<Location> getVerticalCircle(Location center, double radius, int amount, int rotateDegrees) {
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<>();
        for(int i = 0;i < amount; i++) {
            double angle = i * increment;

            Vector offset = rotateVectorAroundY(center.getDirection().clone(), rotateDegrees).multiply(Math.cos(angle) * radius);
            offset.setY(Math.sin(angle) * radius);
            locations.add(center.clone().add(offset));
        }
        return locations;
    }

    public static List<Block> get2DRectangleBlock(Location center, int depth, int width){
        List<Block> list = Lists.newArrayList();

        for (int x = center.getBlockX()-depth/2; x < center.getBlockX()+depth/2; x++) {
            for (int z = center.getBlockZ()-width/2; z < center.getBlockZ()+width/2; z++) {
                list.add(center.getWorld().getBlockAt(x, center.getBlockY(), z));
            }
        }

        return list;
    }

    public static List<Block> get2DCircleBlock(Location center, int radius){
        return get2DCircleBlock(center, radius, false);
    }

    public static List<Block> get2DCircleBlock(Location center, int radius, boolean avoidAir){
        return get2DCircleBlock(center, radius, avoidAir, null);
    }

    public static List<Block> get2DCircleBlock(Location center, int radius, boolean avoidAir, Consumer<Block> consumer){
        List<Block> list = Lists.newArrayList();

        for (int x = center.getBlockX()-radius; x < center.getBlockX()+radius; x++) {
            for (int z = center.getBlockZ()-radius; z < center.getBlockZ()+radius; z++) {
                Block block = center.getWorld().getBlockAt(x, center.getBlockY(), z);
                if(distanceSquared(block.getLocation(), center) <= NumberConversions.square(radius)){
                    if(avoidAir && block.getType() == Material.AIR){
                        continue;
                    }
                    if(consumer != null){
                        consumer.accept(block);
                    }
                    list.add(block);
                }
            }
        }

        return list;
    }

    public static List<Block> get3DEmptyRectangle(Location center, int depth, int width, int height){
        List<Block> list = Lists.newArrayList();

        list.addAll(get2DRectangleBlock(center, depth, width));

        int z = center.getBlockZ()-width/2;
        for (int i = 0; i < 2; i++) {
            for (int x = center.getBlockX()-depth/2; x < center.getBlockX()+depth/2+1; x++) {
                for (int y = center.getBlockY(); y < center.getBlockY()+height; y++) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (!list.contains(block)) {
                        list.add(block);
                    }
                }
            }
            z += width;
        }

        int x = center.getBlockX()-depth/2;
        for (int i = 0; i < 2; i++) {
            for (z = center.getBlockZ()-width/2; z < center.getBlockZ()+width/2+1; z++) {
                for (int y = center.getBlockY(); y < center.getBlockY()+height; y++) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (!list.contains(block)) {
                        list.add(block);
                    }
                }
            }
            x += depth;
        }

        return list;
    }

    public static List<Block> get3DCylinder(Location center, int radius, int height){
        return get3DCylinder(center, radius, height, false);
    }

    public static List<Block> get3DCylinder(Location center, int radius, int height, boolean avoidAir){
        return get3DCylinder(center, radius, height, avoidAir, null);
    }

    public static List<Block> get3DCylinder(Location center, int radius, int height, boolean avoidAir, Consumer<Block> consumer){
        List<Block> list = Lists.newArrayList();

        for (int y = Math.min(center.getBlockY()+height, center.getWorld().getMaxHeight()); y >= center.getBlockY() ; y--) {
            Location location = center.clone();
            location.setY(y);
            list.addAll(get2DCircleBlock(location, radius, avoidAir, consumer));
        }

        return list;
    }

    public static Vector rotateVectorAroundY(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);

        double currentX = vector.getX();
        double currentZ = vector.getZ();

        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);

        return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
    }

    public static double offSet(double d, int offSet){
        double dd = RANDOM.nextInt(offSet)/100 + RANDOM.nextDouble();
        return d+(RANDOM.nextBoolean() ? - dd: dd);
    }

    public static Location offSet(Location location, int offSet){
        location.setX(offSet(location.getX(), offSet));
        location.setY(offSet(location.getY(), offSet));
        location.setZ(offSet(location.getZ(), offSet));
        return location;
    }

    public static Block[] nearBlocks(Block block){
        return new Block[]{block.getRelative(0, 1, 0),
                block.getRelative(0, -1, 0),
                block.getRelative(1, 0, 0),
                block.getRelative(0, 0, 1),
                block.getRelative(0, 0, -1),
                block.getRelative(-1, 0, 0)};
    }

    public enum Axis {

        X, Y, Z

    }
}
