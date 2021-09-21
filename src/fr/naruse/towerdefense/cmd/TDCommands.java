package fr.naruse.towerdefense.cmd;

import fr.naruse.towerdefense.main.TowerDefensePlugin;
import fr.naruse.towerdefense.storage.ResourcesStorage;
import fr.naruse.towerdefense.timer.GameTimer;
import fr.naruse.towerdefense.unit.AbstractUnit;
import fr.naruse.towerdefense.utils.async.CollectionManager;
import fr.naruse.towerdefense.wave.Waves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record TDCommands(TowerDefensePlugin pl) implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(!(sender instanceof Player)){
            return sendMessage(sender, "§cOnly players!");
        }
        Player p = (Player) sender;

        if(!p.hasPermission("towerdefense")){
            return sendMessage(sender, "§cYou do not have the permission.");
        }

        if(args.length == 0){
            sendMessage(sender, "§6/§7td upgradeAll");
            sendMessage(sender, "§6/§7td setWave <Int>");
            sendMessage(sender, "§6/§7td giveResources");
            return sendMessage(sender, "§6/§7td speed");
        }

        if(args[0].equalsIgnoreCase("speed")){
            GameTimer.setTimer(5);
            return sendMessage(sender, "§aDone.");
        }

        if(args[0].equalsIgnoreCase("giveResources")){
            ResourcesStorage.addGold(Integer.MAX_VALUE/2-ResourcesStorage.getGold());
            ResourcesStorage.addWood(Integer.MAX_VALUE/2-ResourcesStorage.getWood());
            ResourcesStorage.addStone(Integer.MAX_VALUE/2-ResourcesStorage.getStone());
            return sendMessage(sender, "§aDone.");
        }

        if(args[0].equalsIgnoreCase("setWave")){
            int i = 0;
            if(args.length >= 2){
                try{
                    i = Integer.valueOf(args[1]);
                }catch (Exception e){}
            }

            Waves.setCurrentWave(i);
            return sendMessage(sender, "§aDone.");
        }

        if(args[0].equalsIgnoreCase("upgradeAll")){
            Runnable runnable = () -> {
                for (AbstractUnit unit : AbstractUnit.getSpawnedUnitsList()) {
                    while (unit.upgrade(p)) ;
                }
            };
            CollectionManager.SECOND_THREAD_RUNNABLE_SET.add(runnable);
            return sendMessage(sender, "§aDone.");
        }

        return false;
    }

    private boolean sendMessage(CommandSender sender, String msg){
        sender.sendMessage(msg);
        return true;
    }

}
