package fr.naruse.towerdefense.event;

import com.google.common.collect.Sets;
import fr.naruse.towerdefense.board.ScoreboardSign;
import fr.naruse.towerdefense.entity.AbstractEntity;
import fr.naruse.towerdefense.game.GameStatus;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.Axe;
import fr.naruse.towerdefense.unit.AbstractUnit;
import fr.naruse.towerdefense.utils.TDUtils;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.NumberConversions;

import java.util.List;

public record Listeners(TowerDefensePlugin pl) implements Listener {

    @EventHandler
    public void preJoin(PlayerPreLoginEvent e){
        if(WorldUtils.isCalculating()){
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "§7Please wait a little bit, I'm still calculating the arena!");
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(GameStatus.WAIT.isCurrentStatus()){
            this.pl.getPlayerInGame().add(p);
        }else{
            p.setGameMode(GameMode.SPECTATOR);
        }
        p.teleport(WorldUtils.getPlatformLocation());

        if(!WorldUtils.isBaseTerraformDone()){
            p.sendMessage("§7Your world is still in construction, please be patient...");
        }

        ScoreboardSign.apply(p);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e){
        Entity entity = e.getEntity();
        e.getDrops().clear();
        if(this.isInside(entity.getLocation(), 51)){
            return;
        }

        for (AbstractEntity entitySet : Sets.newHashSet(AbstractEntity.getAbstractEntitySets())) {
            entitySet.onDie(entity);
        }
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e){
        Entity entity = e.getEntity();

        boolean inside = this.isInside(entity.getLocation(), 51);
        if(!inside){
            e.setCancelled(true);
            return;
        }

        if(!(entity instanceof Item) && !(entity instanceof ArmorStand) && inside && entity.getLocation().getY() <= WorldUtils.getPlatformLocation().getY()){
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e){
        if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
            e.setDamage(0);
        }
    }

    @EventHandler
    public void pickup(PlayerAttemptPickupItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent e){
        e.setCancelled(true);

        List<AbstractUnit> list = AbstractUnit.getSpawnedUnitsSyncList();
        for (AbstractUnit unit : list) {
            unit.getListener().breakBlock(e);
        }
    }

    @EventHandler
    public void blockDamage(BlockDamageEvent e){
        if(Axe.dig(e.getPlayer(), e.getItemInHand(), e.getBlock())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void placeBlock(PlayerInteractEvent e){
        if(e.getClickedBlock() == null){
            return;
        }
        List<AbstractUnit> list = AbstractUnit.getSpawnedUnitsSyncList();
        for (AbstractUnit unit : list) {
            if(unit.containsBlock(e.getClickedBlock())){
                unit.openInventory(e.getPlayer());
                break;
            }
        }

        if(e.getItem() == null){
            return;
        }
        Material material = e.getItem().getType();
        e.setCancelled(true);

        if(!this.isInside(e.getClickedBlock().getLocation(), 45)){
            return;
        }

        if(Axe.dig(e.getPlayer(), e.getItem(), e.getClickedBlock())){
            return;
        }

        list = AbstractUnit.getUnSpawnedUnitsSyncList();
        for (AbstractUnit unit : list) {
            if(unit.getListener().placeBlock(e, material)){
                break;
            }
        }
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void EntityChangeBlockEvent (EntityChangeBlockEvent event) {
        if(event.getEntityType() == EntityType.FALLING_BLOCK){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player && e.getWhoClicked().getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    private boolean isInside(Location a, double distance){
        return TDUtils.distanceSquared(a, WorldUtils.getMainLocation(), TDUtils.Axis.X, TDUtils.Axis.Z) <= NumberConversions.square(distance);
    }

}
