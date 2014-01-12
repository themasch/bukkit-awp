package it.masch.bukkit.awp;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

//order by name
class WarpPoint implements Comparable<WarpPoint>
{

    public static final String NAME = "name";
    public static final String OWNER_NAME = "ownername";
    public static final String LOCATION = "location";
    public static final String VISIBILITY = "visibility";
    public static final String INVITED = "invited";

    private String name;
    private Location location;
    private String ownerName;
    private boolean isPublic = true;
    private Set<String> invited = new HashSet<String>();

    public WarpPoint(String name, String ownerName, Location loc)
    {
        this.name = name;
        this.ownerName = ownerName;
        this.location = loc;
    }

    public Location getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public boolean isOwner(OfflinePlayer player)
    {
        return getOwner().equals(player);
    }

    public OfflinePlayer getOwner()
    {
        return Bukkit.getServer().getOfflinePlayer(ownerName);
    }

    public void invite(Collection<OfflinePlayer> players)
    {
        for (OfflinePlayer player : players) {
            invited.add(player.getName());
        }
    }

    public void uninvite(Collection<OfflinePlayer> players)
    {
        for (OfflinePlayer player : players) {
            invited.remove(player.getName());
        }
    }

    public void setPublic()
    {
        setPublic(true);
    }

    public void setPrivate()
    {
        setPublic(false);
    }

    public void setPublic(boolean publ)
    {
        isPublic = publ;
    }

    public boolean getPublic()
    {
        return isPublic;
    }

    public boolean isAllowed(OfflinePlayer player)
    {
        return isPublic || ownerName.equals(player.getName())
                || invited.contains(player.getName());
    }

    public Map<String, String> toConfig()
    {
        Map<String, String> config = new HashMap<String, String>();
        config.put(NAME, name);
        config.put(OWNER_NAME, ownerName);
        String loc = location.getWorld().getName() + ",";
        loc += location.getX() + "," + location.getY() + "," + location.getZ()
                + ",";
        loc += location.getYaw() + "," + location.getPitch();
        config.put(LOCATION, loc);
        config.put(VISIBILITY, "" + isPublic);
        String inv = invited + "";
        inv = inv.substring(1, inv.length() - 1);
        config.put(INVITED, inv);
        return config;
    }

    public static WarpPoint fromConfig(Map<String, String> config)
    {
        String locString = config.get(LOCATION);
        String[] locArray = locString.split(",");
        World world = Bukkit.getServer().getWorld(locArray[0]);
        double[] pos;
        float[] dir;
        try {
            pos = new double[] { new Double(locArray[1]),
                    new Double(locArray[2]), new Double(locArray[3]) };
            dir = new float[] { new Float(locArray[4]), new Float(locArray[5]) };
        } catch (NumberFormatException nfe) {
            return null;
        }
        Location loc = new Location(world, pos[0], pos[1], pos[2], dir[0],
                dir[1]);
        WarpPoint wp = new WarpPoint(config.get(NAME), config.get(OWNER_NAME),
                loc);
        wp.setPublic(new Boolean(config.get(VISIBILITY)));
        wp.invited.addAll(Arrays.asList(config.get(INVITED).split(",")));
        wp.invited.remove("");
        return wp;
    }

    @Override
    public int compareTo(WarpPoint o)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
