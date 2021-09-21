package fr.naruse.towerdefense.unit.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public final class ModelUnit implements IEffect {

    private final List<Location> list = Lists.newArrayList();
    private final Map<ModelLocation, Material> materialMap;

    public ModelUnit(Map<ModelLocation, Material> materialMap) {
        this.materialMap = materialMap;
    }

    public boolean place(Location center) {
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> this.place(center));
            return true;
        }
        for (ModelLocation modelLocation : materialMap.keySet()) {
            Location location = center.clone().add(modelLocation.getX(), modelLocation.getY(), modelLocation.getZ());
            if(location.getBlock().getType() != Material.AIR){
                return false;
            }
        }


        Map<Location, Material> map = Maps.newHashMap();

        materialMap.forEach((modelLocation, material) -> {
            Location location = center.clone().add(modelLocation.getX(), modelLocation.getY(), modelLocation.getZ());
            map.put(location, material);
            list.add(location);
        });

        Runnable runnable = () -> map.forEach((location, material) -> location.getBlock().setType(material));
        if(Bukkit.isPrimaryThread()){
            runnable.run();
        }else{
            ThreadGlobal.runSync(runnable);
        }

        return true;
    }

    @Override
    public void kill() {
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> this.kill());
            return;
        }

        for (Location location : list) {
            location.getBlock().setType(Material.AIR);
        }
    }

    public List<Location> getLocations() {
        return list;
    }

    public ModelUnit clone(){
        return new ModelUnit(Maps.newHashMap(this.materialMap));
    }

    public static class Builder {

        public static Builder init() {
            return new Builder();
        }

        private final Map<ModelLocation, Material> materialMap = Maps.newHashMap();

        public Builder addBlock(Material material, int x, int y, int z) {
            this.materialMap.put(new ModelLocation(x, y, z), material);
            return this;
        }

        public Builder addSquare(Material material, int y){
            return addBlock(material, 0, y, 0)
                    .addBlock(material, -1, y, 1)
                    .addBlock(material, 0, y, 1)
                    .addBlock(material, 1, y, 1)
                    .addBlock(material, -1, y, 0)
                    .addBlock(material, 1, y, 0)
                    .addBlock(material, -1, y, -1)
                    .addBlock(material, 0, y, -1)
                    .addBlock(material, 1, y, -1);
        }

        public Builder addLittleSquare(Material material, int y){
            return addBlock(material, 0, y, 0)
                    .addBlock(material, 1, y, 0)
                    .addBlock(material, 0, y, 1)
                    .addBlock(material, 1, y, 1);
        }

        public Builder addBlock(Material material, ModelLocation modelLocation) {
            this.materialMap.put(modelLocation, material);
            return this;
        }

        public ModelUnit build() {
            return new ModelUnit(this.materialMap);
        }
    }

}
