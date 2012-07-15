package it.masch.bukkit.awp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AwpCommandExecutor implements CommandExecutor {
	private final JavaPlugin plugin;
	private final FileConfiguration config;
	private ConfigurationSection warps;
	private final Plugin tpp;

	public AwpCommandExecutor(JavaPlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.warps = this.config.getConfigurationSection("warps");
		if (this.warps == null) {
			this.warps = this.config.createSection("warps");
		}
		this.tpp = plugin.getServer().getPluginManager()
				.getPlugin("TelePlusPlus");
		if (this.tpp == null || !this.tpp.isEnabled()) {
			throw new RuntimeException(
					"TelePlusPlus not active on this server!");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return false;
		}

		if (args.length < 1) {
			sender.sendMessage("help?!");
		} else {
			if (args[0].equalsIgnoreCase("create")) {
				this.doCreate(player, args);
			} else {
				this.doWarp(player, args);

			}
		}

		return true;
	}

	public void doWarp(Player pl, String[] args) {
		if (!pl.hasPermission("awp.warp.own")
				&& !pl.hasPermission("awp.warp.public")) {
			pl.sendMessage("no permission to warp, buddy");
		}

		String wp = args[0];
		String owner = null;
		String player = pl.getName();
		if (wp.indexOf(".") != -1) {
			String ownerName = wp.substring(0, wp.indexOf("."));
			for (String name : warps.getKeys(false)) {
				if (name.toLowerCase().startsWith(ownerName)) {
					owner = name;
					wp = wp.substring(wp.indexOf(".") + 1);
					break;
				}
			}
		}

		String wpOwner = "";

		while (true) {
			if ((owner == null || owner.equals(player))
					&& pl.hasPermission("awp.warp.own")
					&& warps.contains(player + "." + wp)) {
				wpOwner = player;
				break;
			}

			if (player.equals(owner)) {
				if (pl.hasPermission("awp.warp.own"))
					pl.sendMessage("sorry, warp point not found");
				else
					pl.sendMessage("no permission to warp, buddy");
				return;
			}

			if (!pl.hasPermission("awp.warp.public")) {
				pl.sendMessage("no permission to warp, buddy");
				return;
			}

			if (owner == null) {
				for (String name : warps.getKeys(false)) {
					if (warps.contains(name + "." + wp)) {
						// TODO (isPublic y/n)
						wpOwner = name;
						break;
					}
				}
				pl.sendMessage("sorry, warp point not found");
				return;
			}

			if (!warps.contains(owner + "." + wp)) {
				pl.sendMessage("sorry, warp point not found");
				return;
			}

			// TODO (ispublic y/n)
			wpOwner = owner;
			break;
		}
		try {
			WarpPoint warp = new WarpPoint(this.plugin.getServer(),
					this.warps.getString(wpOwner + "." + wp));
			String[] argsToTPP = { warp.getWorld().getName(), "" + warp.getX(),
					"" + warp.getY(), "" + warp.getZ() };
			this.tpp.onCommand(pl, new PluginsCommand("tp"), "tp", argsToTPP);
			// TODO direction/view angle

		} catch (Exception e) {
			pl.sendMessage("broken data, Junge!");
		}
	}

	public void doCreate(Player pl, String[] args) {
		if (!pl.hasPermission("awp.create")) {
			pl.sendMessage("missing permissions! (awp.create)");
			return;
		}
		String name = "";
		if (args.length <= 1) {
			pl.sendMessage("please provide a name for this warp point");
			pl.sendMessage("usage: /awp create <name>");
			return;
		}
		name = pl.getName().toLowerCase() + "." + args[1];
		if (this.warps.contains(name)) {
			pl.sendMessage("this warp point already exists.");
			pl.sendMessage("please use /awp set to change a warp points location.");
		}
		WarpPoint wp = new WarpPoint(pl.getLocation());
		String wps = wp.toString();
		this.warps.set(name, wps);

		pl.sendMessage("warp created");
		this.saveConfig();
	}

	public void saveConfig() {
		this.plugin.saveConfig();
	}

}
