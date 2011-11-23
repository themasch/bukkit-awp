package it.masch.bukkit.awp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

public class AwpCommandExecutor implements CommandExecutor
{
    private JavaPlugin plugin;
    private FileConfiguration config; 
    private ConfigurationSection warps;

    public AwpCommandExecutor(JavaPlugin plugin) 
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.warps  = this.config.getConfigurationSection("warps");
        if(this.warps == null) {
            this.warps = this.config.createSection("warps");
        }
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
                this.doWarp(player, args); 

            }
        }

        return true;                             
    }

    public void doWarp(Player pl, String[] args) 
    {
        String wp = args[0];
        Player owner = null;
        if(wp.indexOf(".") != -1) {
            String ownerName = wp.substring(0, wp.indexOf("."));
            owner = this.plugin.getServer().matchPlayer(ownerName).get(0);
            wp = wp.substring(wp.indexOf(".")+1);
        }
        if(owner == null) {
            owner = pl;
        }
        try { 
            wp = owner.getName().toLowerCase() + "." + wp;
            if(owner == pl) {
                if(pl.hasPermission("awp.warp.own") && this.warps.contains(wp)) {
                    pl.teleport(new WarpPoint(this.plugin.getServer(), this.warps.getString(wp)));
                    return;
                }
                pl.sendMessage("looking for public warp points");
            } else {
                if(pl.hasPermission("awp.warp.public") && this.warps.contains(wp)) {
                    pl.teleport(new WarpPoint(this.plugin.getServer(), this.warps.getString(wp)));
                    return;
                }
                pl.sendMessage("looking for public warp point " + wp);
            }
        } catch(Exception e) {

        }
    }

    public void doCreate(Player pl, String[] args) 
    {
        if(!pl.hasPermission("awp.create")) {
            pl.sendMessage("missing permissions! (awp.create)");
            return;
        }
        String name = "";
        if(args.length <= 1) {
            pl.sendMessage("please provide a name for this warp point");
            pl.sendMessage("usage: /awp create <name>");
            return;
        } 
        name = pl.getName().toLowerCase() + "." + args[1];
        if(this.warps.contains(name)) {
            pl.sendMessage("this warp point already exists.");
            pl.sendMessage("please use /awp set to change a warp points location.");
        }
        WarpPoint wp = new WarpPoint(pl.getLocation());
        String wps   = wp.toString();
        this.warps.set(name, wps); 

        pl.sendMessage("warp created");
        this.saveConfig();
    }

    public void saveConfig()
    {
        this.plugin.saveConfig();
    }

}
