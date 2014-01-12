package it.masch.bukkit.awp;

import java.util.List;
import java.util.Set;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;


class WarpPoint implements Comparable<WarpPoint>
{

  public static final String NAME = "name";
  public static final String OWNER_NAME = "ownername";
  public static final String LOCATION = "location";

  private String name;
  private Location location;
  private String ownerName;
  private boolean isPublic;
  private Set<String> invited = new HashSet<String>();

  public WarpPoint(String name, String ownerName, Location loc)
  {
    this.name = name;
    this.ownerName = ownerName;
    this.location  = loc;
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
   return Bukkit.getServer().getOfflinePlayer(ownerName)
  }

  public void invite(Set<OfflinePlayer> players)
  {
    for (OfflinePlayer player : players){
      invited.add(player.getName());
    }
  }

  public void uninvite(Set<OfflinePlayer> players)
  {
    for(OfflinePlayer player : players) {
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
    return isPublic ||
           ownerName.equals(player.getName()) ||
           invited.contains(player.getName());
  }

  public ConfigurationSection toConfig()
  {
    return null;
  }

  public static WarpPoint fromConfig(Map<String,String> config)
  {
    String locString = config.get(LOCATION);
    String[] locArray = locString.split(",");
    World world = Bukkit.getServer().getWorld(locArray[0]);
    double[] pos;
    float[] dir;
    try {
    pos = new double[]{new Double(locArray[1]),
                       new Double(locArray[2]),
                       new Double(locArray[3])};
    dir = new float[]{new Float(locArray[4]),
                      new Float(locArray[5])};
    } catch (NumberFormatException nfe){
      return null
    }
    Location loc = new Location();
    return null;
  }
}
