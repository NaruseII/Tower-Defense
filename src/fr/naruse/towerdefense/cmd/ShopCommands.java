package fr.naruse.towerdefense.cmd;

import fr.naruse.towerdefense.game.GameStatus;
import fr.naruse.towerdefense.inventory.InventoryShopMain;
import fr.naruse.towerdefense.main.TowerDefensePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record ShopCommands(TowerDefensePlugin pl) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player p = (Player) sender;

        if(GameStatus.GAME.isCurrentStatus()){
            new InventoryShopMain(this.pl, p);
        }

        return false;
    }

}
