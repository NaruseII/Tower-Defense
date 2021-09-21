package fr.naruse.towerdefense.unit.level;

import com.google.common.collect.Maps;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.unit.model.ModelUnit;

import java.util.Map;

public class Levels {

    private final Map<Integer, Level> levelMap = Maps.newHashMap();

    private Level currentLevel;

    public void registerNewLevel(Level level){
        if(level.getLevel() == 1){
            this.currentLevel = level;
        }
        this.levelMap.put(level.getLevel(), level);
    }

    public void upgrade(){
        this.currentLevel = this.getNextLevel();
        ResourcesStorage.addGold(-this.currentLevel.getGoldCost());
        ResourcesStorage.addWood(-this.currentLevel.getWoodCost());
        ResourcesStorage.addStone(-this.currentLevel.getStoneCost());
    }

    public Level getNextLevel(){
        return this.levelMap.get(this.currentLevel.getLevel()+1);
    }

    public Level getCurrentLevel(){
        return this.currentLevel;
    }
    public static class Builder {

        public static Builder init(int level, int goldCost, int woodCost, int stoneCost){
            return new Builder(level, goldCost, woodCost, stoneCost);
        }

        private final Map<LevelAttribute, Object> attributeMap = Maps.newHashMap();
        private final int level;
        private final int goldCost;
        private final int woodCost;
        private final int stoneCost;

        private ModelUnit modelUnit;
        private Runnable runnable;

        Builder(int level, int goldCost, int woodCost, int stoneCost) {
            this.level = level;
            this.goldCost = goldCost;
            this.woodCost = woodCost;
            this.stoneCost = stoneCost;
        }

        public Builder registerAttribute(LevelAttribute attribute, Object value){
            this.attributeMap.put(attribute, value);
            return this;
        }

        public Builder setNewModel(ModelUnit modelUnit){
            this.modelUnit = modelUnit;
            return this;
        }

        public Builder setRunnableOnUpgrade(Runnable runnable){
            this.runnable = runnable;
            return this;
        }

        public Level build(){
            return new Level(this.level, this.attributeMap, this.goldCost, this.woodCost, this.stoneCost, this.modelUnit, this.runnable);
        }

    }

    public static final class Level {

        private final int level;
        private final Map<LevelAttribute, Object> attributeMap;
        private final int goldCost;
        private final int woodCost;
        private final int stoneCost;

        private ModelUnit modelUnit;
        private Runnable runnable;

        public Level(int level, Map<LevelAttribute, Object> attributeMap, int goldCost, int woodCost, int stoneCost, ModelUnit modelUnit, Runnable runnable) {
            this.level = level;
            this.attributeMap = attributeMap;
            this.goldCost = goldCost;
            this.woodCost = woodCost;
            this.stoneCost = stoneCost;
            this.modelUnit = modelUnit;
            this.runnable = runnable;
        }

        public <T> T getValue(LevelAttribute attribute) {
            return (T) this.attributeMap.get(attribute);
        }

        public int getLevel() {
            return level;
        }

        public Map<LevelAttribute, Object> attributeMap() {
            return attributeMap;
        }

        public int getGoldCost() {
            return goldCost;
        }

        public int getStoneCost() {
            return stoneCost;
        }

        public int getWoodCost() {
            return woodCost;
        }

        public ModelUnit getModelUnit() {
            return modelUnit;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public boolean canUpgrade(){
            if(ResourcesStorage.getGold() < this.goldCost || ResourcesStorage.getWood() < this.woodCost || ResourcesStorage.getStone() < this.stoneCost){
                return false;
            }
            return true;
        }
    }
}
