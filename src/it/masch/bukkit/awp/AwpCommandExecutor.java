package it.masch.bukkit.awp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

public class AwpCommandExecutor implements CommandExecutor
{
    private JavaPlugin plugin;
    private FileConfiguration config; 

    public AwpCommandExecutor(JavaPlugin plugin) 
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            return false;
        }

        if(args.length < 1) { 
            sender.sendMessage("help?!");
        } else {
            if(args[0].equalsIgnoreCase("create")) {
                this.doCreate(player, args);
            }
            else {
                sender.sendMessage(args[0]);
            }
        }

        return true;                             
    }

    public void doCreate(Player pl, String[] args) 
    {
        if(!pl.hasPermission("awp.create")) {
            pl.sendMessage("mission permissions! (awp.create)");
            return;
        }
        String name = "";
        if(args.length <= 1) {
            pl.sendMessage("please provide a name for this warp point");
            pl.sendMessage("usage: /awp create <name>");
            return;
        } 
        name = args[1];
        WarpPoint wp = new WarpPoint(pl.getLocation());
        String wps   = wp.toString();
        this.config.set(pl.getName() + "." + name, wps); 

        pl.sendMessage(this.config.getString(pl.getName() + "." + name));
    }

}
