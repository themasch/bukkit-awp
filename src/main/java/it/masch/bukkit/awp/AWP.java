package it.masch.bukkit.awp;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;

public class AWP extends JavaPlugin {
	public static final String VERSION = "0.0.1";
	public static final String NAME = "AWP";

	private Logger log = Logger.getLogger("Minecraft");
	private CommandExecutor cmd;

	public void onEnable() {
		log.info("[" + NAME + "] version " + VERSION + " enabled");
		this.cmd = new AwpCommandExecutor(this);
		this.getCommand("awp").setExecutor(this.cmd);
	}

	public void onDisable() {
		log.info("[" + NAME + "] disabled");
	}

}
