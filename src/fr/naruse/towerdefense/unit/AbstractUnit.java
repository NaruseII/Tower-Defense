package fr.naruse.towerdefense.unit;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.effect.IEffect;
import fr.naruse.towerdefense.inventory.InventoryUnitMain;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.unit.level.LevelAttribute;
import fr.naruse.towerdefense.unit.level.Levels;
import fr.naruse.towerdefense.unit.model.ModelUnit;
import fr.naruse.towerdefense.utils.ParticleUtils;
import fr.naruse.towerdefense.utils.async.AsyncList;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import net.minecraft.core.particles.ParticleParamBlock;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractUnit implements IEffect {

    private static final AsyncList<AbstractUnit> unitsList = new AsyncList();

    public static AsyncList<AbstractUnit> getUnitsList() {
        return unitsList;
    }

    public static List<AbstractUnit> getSpawnedUnitsList() {
        return unitsList.getList().stream().filter(unit -> unit.isSpawned()).collect(Collectors.toList());
    }

    public static List<AbstractUnit> getUnSpawnedUnitsList() {
        return unitsList.getList().stream().filter(unit -> !unit.isSpawned()).collect(Collectors.toList());
    }

    public static List<AbstractUnit> getSpawnedUnitsSyncList() {
        return unitsList.getSyncList().stream().filter(unit -> unit.isSpawned()).collect(Collectors.toList());
    }

    public static List<AbstractUnit> getUnSpawnedUnitsSyncList() {
        return unitsList.getSyncList().stream().filter(unit -> !unit.isSpawned()).collect(Collectors.toList());
    }

    protected final TowerDefensePlugin pl;
    private final Listeners listener = new Listeners();
    private final Set<IEffect> effectSet = Sets.newHashSet();
    protected final Levels levels = new Levels();

    private ModelUnit model;
    private ArmorStand nameTag;
    protected Location location;
    protected double health;
    private boolean isDead = false;
    private long lastAttackTime = 0;

    public AbstractUnit(TowerDefensePlugin pl) {
        this.pl = pl;

        unitsList.add(this);
    }

    abstract void onPlace(Player p, Block block, ItemStack item);

    public abstract ItemStack getItemStack();

    public abstract AbstractUnit newInstance();

    public boolean containsBlock(Block clickedBlock){
        return this.getModel().getLocations().contains(clickedBlock.getLocation());
    }

    @Override
    public void kill() {
        for (IEffect effect : this.effectSet) {
            effect.kill();
        }
        this.effectSet.clear();
        if(this.nameTag != null){
            this.nameTag.remove();
        }
        this.isDead = true;
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(() -> unitsList.remove(this));
    }

    public boolean upgrade(Player p){
        Levels.Level nextLevel = this.levels.getNextLevel();
        if(nextLevel == null){
            p.sendMessage("§cYou reached the max level!");
            return false;
        }
        if(!nextLevel.canUpgrade()){
            p.sendMessage("§cYou do not have enough resources!");
            return false;
        }
        if(!(this instanceof UnitCentral)){
            if(UnitCentral.get().getLevels().getCurrentLevel().getLevel() < nextLevel.getLevel()){
                p.sendMessage("§cYour Central Unit needs to be updated first!");
                return false;
            }
        }

        this.levels.upgrade();
        if(nextLevel.getRunnable() != null){
            nextLevel.getRunnable().run();
        }
        if(nextLevel.getModelUnit() != null){
            this.getModel().kill();
            this.effectSet.remove(this.getModel());
            this.setModel(nextLevel.getModelUnit());
            this.getModel().place(location);
            this.registerEffect(this.getModel());
            this.health = this.getMaxHealth();
        }

        return true;
    }

    public void hurt(double damage){
        this.lastAttackTime = System.currentTimeMillis();
        this.health = Math.max(this.health-damage, 0);

        if(this.health == 0){
            this.kill();
            return;
        }

        this.displayNameTag();

        ParticleUtils.buildParticle(location.clone().add(0, 1.5, 0), new ParticleParamBlock(ParticleUtils.ParticleType.BLOCK, Blocks.fJ.getBlockData()), 1, 1, 1, 8, 1).toAll();

        Runnable runnable = () -> location.getWorld().playSound(location, Sound.ENTITY_PLAYER_HURT, 1, 1);
        if(Bukkit.isPrimaryThread()){
            runnable.run();
        }else{
            ThreadGlobal.runSync(runnable);
        }
    }

    public void heal(){
        this.heal(this.getMaxHealth());
    }

    public void heal(double heal){
        if(this.health == this.getMaxHealth()){
            return;
        }
        this.health = Math.min(this.health+heal, this.getMaxHealth());
        this.displayNameTag();

        Runnable runnable = () -> ParticleUtils.buildParticle(location.clone().add(0, 1.5, 0), ParticleUtils.ParticleType.HEART, 1, 1, 1, 4, 1).toAll();
        CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
    }

    protected void displayNameTag(){
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> displayNameTag());
            return;
        }

        if(this.nameTag == null){
            this.spawnNameTag();
        }

        if(this.health == this.getMaxHealth()){
            this.nameTag.setCustomNameVisible(false);
        }else{
            this.nameTag.setCustomNameVisible(true);

            StringBuilder builder = new StringBuilder();

            int charCount = 100;
            int green = (int) (this.health*charCount/this.getMaxHealth());
            int i;
            for (i = 0; i < green; i++) {
                builder.append("§a|");
            }

            for (; i < charCount; i++) {
                builder.append("§c|");
            }

            this.nameTag.setCustomName(builder.toString());
        }
    }

    protected void spawnNameTag(){
        this.nameTag = (ArmorStand) location.getWorld().spawnEntity(location.clone().toCenterLocation().add(0, this instanceof UnitCentral ? 4 : 3, 0), EntityType.ARMOR_STAND);
        this.nameTag.setVisible(false);
        this.nameTag.setInvulnerable(true);
        this.nameTag.setGravity(false);
        this.nameTag.setSmall(true);
    }

    public void giveItem(Player p){
        p.getInventory().addItem(this.getItemStack());
    }

    protected void registerEffect(IEffect effect){
        this.effectSet.add(effect);
    }

    protected void removeEffect(Class<? extends IEffect> clazz){
        Set<IEffect> set = Sets.newHashSet(this.effectSet);
        for (IEffect effect : set) {
            if(effect.getClass().isAssignableFrom(clazz)){
                effect.kill();
                this.effectSet.remove(effect);
            }
        }
    }

    protected ItemStack buildItem(Material material, String name, boolean enchant) {
        return this.buildItem(material, 1, name, enchant, null);
    }

    protected ItemStack buildItem(Material material, int amount, String name, boolean enchant) {
        return this.buildItem(material, amount, name, enchant, null);
    }

    protected ItemStack buildItem(Material material, int amount, String name, boolean enchant, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        if (enchant) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS});
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void openInventory(Player p) {
        if(!Bukkit.isPrimaryThread()){
            ThreadGlobal.runSync(() -> this.openInventory(p));
            return;
        }

        new InventoryUnitMain(this.pl, p, this);
    }

    public Listeners getListener() {
        return listener;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isSpawned(){
        return this.location != null;
    }

    public boolean isDead() {
        return isDead;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public double getHealth() {
        return health;
    }

    public int getHealFactor(){
        return this.levels.getCurrentLevel().getValue(LevelAttribute.HEAL_FACTOR);
    }

    public int getMaxHealth() {
        return this.levels.getCurrentLevel().getValue(LevelAttribute.MAX_HEALTH);
    }

    public Levels getLevels() {
        return levels;
    }

    public ModelUnit getModel(){
        if(this.model == null){
            this.setModel(this.levels.getCurrentLevel().getModelUnit());
        }
        return this.model;
    }

    public void setModel(ModelUnit model) {
        this.model = model;
    }

    public class Listeners {

        public boolean placeBlock(PlayerInteractEvent e, Material material){
            if(material != getItemStack().getType() || e.getClickedBlock().getY() != WorldUtils.getMainLocation().getY()){
                return false;
            }

            Levels.Level level = levels.getCurrentLevel();
            if(!level.canUpgrade()){
                e.getPlayer().sendMessage("§cYou do not have enough resources!");
                return true;
            }

            if(e.getClickedBlock().getType() == Material.CRIMSON_NYLIUM || e.getClickedBlock().getType() == Material.GLASS){
                return true;
            }

            if(!(AbstractUnit.this instanceof UnitCentral) && UnitCentral.get().getLocation().distanceSquared(e.getClickedBlock().getLocation()) >= NumberConversions.square(45)){
                e.getPlayer().sendMessage("§cUnits cannot be more than 45 blocks away from the Central Unit!");
                return true;
            }

            Location loc = e.getClickedBlock().getLocation().clone().add(0, 1, 0);
            if(getModel().place(loc)){
                registerEffect(getModel());
            }else{
                e.getPlayer().sendMessage("§cYou cannot put me here! Not enough space.");
                return true;
            }

            health = getMaxHealth();
            location = loc.clone();
            e.getItem().setAmount(e.getItem().getAmount()-1);

            ResourcesStorage.addGold(-level.getGoldCost());
            ResourcesStorage.addWood(-level.getWoodCost());
            ResourcesStorage.addStone(-level.getStoneCost());

            onPlace(e.getPlayer(), loc.getBlock(), e.getItem());

            return true;
        }

        public void breakBlock(BlockBreakEvent e){

        }

    }

    protected class KillableBlock implements IEffect{

        private final Set<Block> blocks;

        public KillableBlock(Block block) {
            this.blocks = Sets.newHashSet(block);
        }

        public KillableBlock(Set<Block> blocks) {
            this.blocks = blocks;
        }

        @Override
        public void kill() {
            for (Block block : this.blocks) {
                block.setType(Material.AIR);
            }
        }
    }
}
