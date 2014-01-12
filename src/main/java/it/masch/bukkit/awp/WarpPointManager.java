package it.masch.bukkit.awp;

import java.util.List;
import java.util.Set;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

class WarpPointManager  // WarpPointRepository
{
  // Spieler->Name->WarpPoint string nur für lookup, daher beim "parsen"
  private Map<String, Map<String, WarpPoint>> warpPoint;

  public WarpPointManager(ConfigurationSection config)
  {
    // parse it
    for(String player : config.getKeys()) {
      Map<String, WarpPoint> temp = new HashMap<String, WarpPoint>;
      warpPoints.set(player.toLowerCase(), temp);
      List<Map<String, String>> points = config.getMapList(player);
      for(Map<String, String> map: points) {
        WarpPoint point = WarpPoint.fromConfig(map);
        temp.set(point.getName().toLowerCase(), point);
      }
    }
  }

  public WarpPoint findFirst(OfflinePlayer sender, String input, boolean ignorePermissions)
  {
    try {
      return find(sender, input, ignorePermissions).get(0);
    } catch( IndexOutOfBoundsException ex) {
      return null;
    }
  }

  // orderd list (me first)
  public List<WarpPoint> find(OfflinePlayer sender, String input, boolean ignorePermissions)
  {
    // NameDesSpieler.NameDesPunkte a)
    // Namede.NameDesPunktes b)
    // NameDe.Name.Des.Punktes b)
    // NameDesPunktes c)

    // home
    // home.mine

    // awp invite Doofkopp home.*
    // awp public|private home.*

    // awp list Player.home
    // => Player.home.asdf

    // 1. split (.,1)
    // 2. test: [0] player?

    // if(ignorePermissions || wp.isAllowed())
  }

  public List<WarpPoint> findOwnedBy(OfflinePlayer owner, String input)
  {

  }

  public boolean create(String name, OfflinePlayer owner)
  {
    // 1 existiert schon? => return false
    // 2 existiert nicht => erstellen => in Liste => return true
  }

  public boolean delete(String name, OfflinePlayer owner)
  {
  }

  public boolean set(String name, OfflinePlayer owner)
  {
  }

  public boolean setVisibility(String name, OfflinePlayer owner, boolean visible)
  {
  }

  public boolean invite(String name, OfflinePlayer owner, Set<String> players)
  {
    List<WarpPoint> wpList = findOwnedBy(owner,name);
    Set<OfflinePlayer> invited = findPlayers(players);

    for(WarpPoint point: wpList) {
      wp.invite(invited);
    }
  }

  public boolean uninvite(String name, OfflinePlayer owner, Set<String> players)
  {
    List<WarpPoint> wpList = findOwnedBy(owner,name);
    Set<OfflinePlayer> invited = findPlayers(players);

    for(WarpPoint point: wpList) {
      wp.uninvite(invited);
    }
  }

  protected Set<OfflinePlayer> findPlayers(Set<String> names)
  {
    Set<OfflinePlayer> set = new HashSet<OfflinePlayer>();
    OfflinePlayer tmp;
    for(String name: names) {
      tmp = this.findPlayerByName(name);
      if(tmp != null) {
        set.add(tmp);
      }
    }
    return set;
  }

  protected OfflinePlayer findPlayerByName(String name)
  {
    for(String key: warpPoints.keySet()) {
      if(key.startsWith(name.toLowerCase())) {
        return Bukkit.getServer().getOfflinePlayer(key);
      }
    }
    List <OfflinePlayer> candidates = Bukkit.getServer().matchPlayer(name);
    return candidates.isEmpty() ? null : candidates.get(0);
  }
}
