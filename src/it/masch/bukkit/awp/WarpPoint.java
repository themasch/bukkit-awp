package it.masch.bukkit.awp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;

class WarpPoint extends Location {
	private boolean isPublic;
	private List<String> players = null;

	public WarpPoint(Location loc) {
		this(loc, true);
	}

	public WarpPoint(Location loc, boolean isPublic) {
		super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
				loc.getPitch());
		this.setPublic(isPublic);
		if (!isPublic) {
			players = new ArrayList<String>();
		}
	}

	public WarpPoint(Server srv, String str) throws Exception {
		super(null, 0, 0, 0, 0, 0);
		String[] params = str.split(",");
		if (params.length < 6) {
			throw new Exception("Invalid data format.");
		}
		Location loc = new Location(srv.getWorld(params[0]),
				Double.parseDouble(params[1]), // X
				Double.parseDouble(params[2]), // Y
				Double.parseDouble(params[3]), // Z
				Float.valueOf(params[4]).floatValue(), // yaw
				Float.valueOf(params[5]).floatValue() // pitch
		);
		boolean isPublic = params.length == 6 || Boolean.valueOf(params[6]);
		this.setWorld(loc.getWorld());
		this.add(loc);
		this.setYaw(loc.getYaw());
		this.setPitch(loc.getPitch());
		this.setPublic(isPublic);
		if (!this.isPublic()) {
			players = new ArrayList<String>();
			for (int i = 7; i < params.length; i++) {
				players.add(params[i]);
			}
		}
	}

	@Override
	public String toString() {
		String ret = this.getWorld().getName() + "," + this.getX() + ","
				+ this.getY() + "," + this.getZ() + "," + this.getYaw() + ","
				+ this.getPitch();
		if (!this.isPublic()) {
			ret += "," + this.isPublic();
			for (String name : players) {
				ret += "," + name;
			}
		}
		return ret;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		if (isPublic) {
			players = null;
		} else {
			players = new ArrayList<String>();
		}
	}

	public List<String> getPlayers() {
		return players;
	}

	public void addPlayer(String playerName) {
		if (this.isPublic())
			return;
		if (this.getPlayers().contains(playerName))
			return;
		this.players.add(playerName);
	}
}
