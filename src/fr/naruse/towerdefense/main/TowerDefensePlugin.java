package fr.naruse.towerdefense.main;

import com.google.common.collect.Lists;
import fr.naruse.servermanager.core.config.Configuration;
import fr.naruse.towerdefense.board.ScoreboardSign;
import fr.naruse.towerdefense.cmd.ShopCommands;
import fr.naruse.towerdefense.entity.EntityRunner;
import fr.naruse.towerdefense.game.GameStatus;
import fr.naruse.towerdefense.timer.WaitingTimer;
import fr.naruse.towerdefense.cmd.TDCommands;
import fr.naruse.towerdefense.config.ConfigData;
import fr.naruse.towerdefense.event.Listeners;
import fr.naruse.towerdefense.unit.AbstractUnit;
import fr.naruse.towerdefense.unit.UnitCentral;
import fr.naruse.towerdefense.unit.UnitRunner;
import fr.naruse.towerdefense.utils.PlayerUtils;
import fr.naruse.towerdefense.utils.async.ThreadGlobal;
import fr.naruse.towerdefense.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class TowerDefensePlugin extends JavaPlugin {

    private Configuration configuration;
    private List<Player> playerInGame = Lists.newArrayList();

    @Override
    public void onEnable() {
        super.onEnable();
        this.configuration = new Configuration(new File(this.getDataFolder(), "config.json"), this.getClass().getClassLoader().getResourceAsStream("resources/config.json"));
        ConfigData.load(this.configuration);
        ThreadGlobal.launch(this);
        PlayerUtils.setPlugin(this);
        ScoreboardSign.load(this);

        WaitingTimer.start(this);
        WorldUtils.terraformWorldBase(this, Bukkit.getWorlds().get(0));

        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getCommand("td").setExecutor(new TDCommands(this));
        this.getCommand("shop").setExecutor(new ShopCommands(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (AbstractUnit unit : AbstractUnit.getUnitsList().getList()) {
            unit.kill();
        }
        ThreadGlobal.shutdown();
    }

    public void start(){
        GameStatus.GAME.apply();
        PlayerUtils.sendMessage("§aThe game starts!");
        PlayerUtils.sendMessage("§6Enemies wave will start when you will place your central unit!");
        WorldUtils.deleteGlassPlatform();

        Optional<Player> optional = PlayerUtils.findFirst();
        if(optional.isPresent()){
            Player p = optional.get();
            new UnitCentral(this).giveItem(p);
            PlayerUtils.sendMessage("§5"+p.getName()+"§7 you have the central unit. Place it somewhere!");
        }

        new EntityRunner().runTaskTimer(this, 1, 1);
        new UnitRunner().runTaskTimer(this, 20, 20);
    }

    public List<Player> getPlayerInGame() {
        return playerInGame;
    }
}
