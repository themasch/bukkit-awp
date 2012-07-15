package it.masch.bukkit.awp;

import net.sacredlabyrinth.Phaed.TelePlusPlus.TelePlusPlus;
import net.sacredlabyrinth.Phaed.TelePlusPlus.managers.TeleportManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AwpCommandExecutor implements CommandExecutor {
	private final JavaPlugin plugin;
	private final FileConfiguration config;
	private ConfigurationSection warps;
	public final TeleportManager tm;

	public AwpCommandExecutor(JavaPlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.warps = this.config.getConfigurationSection("warps");
		if (this.warps == null) {
			this.warps = this.config.createSection("warps");
		}
		TelePlusPlus tpp = (TelePlusPlus) plugin.getServer().getPluginManager()
				.getPlugin("TelePlusPlus");
		if (tpp == null || !tpp.isEnabled()) {
			throw new RuntimeException(
					"TelePlusPlus not active on this server!");
		}
		this.tm = tpp.tm;
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
			pl.sendMessage(ChatColor.RED + "No permission to warp, buddy.");
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
					pl.sendMessage(ChatColor.RED
							+ "Sorry, warp point not found.");
				else
					pl.sendMessage(ChatColor.RED
							+ "No permission to warp, buddy.");
				return;
			}

			if (!pl.hasPermission("awp.warp.public")) {
				pl.sendMessage(ChatColor.RED + "No permission to warp, buddy.");
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
				pl.sendMessage(ChatColor.RED + "Sorry, warp point not found.");
				return;
			}

			if (!warps.contains(owner + "." + wp)) {
				pl.sendMessage(ChatColor.RED + "Sorry, warp point not found.");
				return;
			}

			// TODO (ispublic y/n)
			wpOwner = owner;
			break;
		}
		try {
			WarpPoint warp = new WarpPoint(this.plugin.getServer(),
					this.warps.getString(wpOwner + "." + wp));
			if (!tm.teleport(pl, warp)) {
				pl.sendMessage(ChatColor.RED
						+ "No free space available for warp");
				return;
			}
			pl.sendMessage(ChatColor.DARK_PURPLE + "Warped to " + wp);

		} catch (Exception e) {
			pl.sendMessage("broken data, Junge!");
		}
	}

	public void doCreate(Player pl, String[] args) {
		if (!pl.hasPermission("awp.create")) {
			pl.sendMessage(ChatColor.RED + "Missing permissions! (awp.create)");
			return;
		}
		String name = "";
		if (args.length <= 1) {
			pl.sendMessage(ChatColor.YELLOW
					+ "Please provide a name for this warp point.");
			pl.sendMessage(ChatColor.YELLOW + "Usage: /awp create <name>");
			return;
		}
		name = pl.getName().toLowerCase() + "." + args[1];
		if (this.warps.contains(name)) {
			pl.sendMessage(ChatColor.YELLOW + "This warp point already exists.");
			pl.sendMessage(ChatColor.YELLOW
					+ "Please use /awp set to change a warp points location.");
		}
		WarpPoint wp = new WarpPoint(pl.getLocation());
		String wps = wp.toString();
		this.warps.set(name, wps);

		pl.sendMessage(ChatColor.DARK_PURPLE + "Warp " + wps + " created.");
		this.saveConfig();
	}

	public void saveConfig() {
		this.plugin.saveConfig();
	}

}
