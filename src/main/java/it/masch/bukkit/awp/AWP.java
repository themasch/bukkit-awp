package it.masch.bukkit.awp;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

public class AWP extends JavaPlugin
{

    private Logger log = Logger.getLogger("Minecraft");
    private CommandExecutor cmd;

    public void onEnable()
    {
        PluginDescriptionFile desc = getDescription();
        log.info("[" + desc.getName() + "#" + desc.getVersion() + "] enabled");
        log.info("AWP ENABLED");
        cmd = new AwpCommandExecutor(this);
        getCommand("awp").setExecutor(cmd);
    }

    public void onDisable()
    {
        PluginDescriptionFile desc = getDescription();
        log.info("[" + desc.getName() + "#" + desc.getVersion() + "] disabled");
    }

}
