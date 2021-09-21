package fr.naruse.towerdefense.utils.world;

import org.bukkit.Location;
import org.bukkit.Material;

public interface ITDResource {

    Location getLocation();

    Location getRandomLocation();

    Material getMaterial();

}
