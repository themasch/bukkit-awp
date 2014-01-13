package it.masch.bukkit.awp;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * a rather simple and dump implementation of the repository pattern
 * to manage warp points in AWP
 *
 * This class is used to retrieve, update and store warp points with
 * an easy to use interface.
 *
 * @package AWP
 * @author  Jan Czogalla <jan.czogalla@udo.edu>
 * @author  Mark Schmale <masch@masch.it>
 * @license MIT
 */
class WarpPointManager
{
    // Spieler->Name->WarpPoint string nur für lookup, daher beim "parsen"
    private Map<String, Map<String, WarpPoint>> warpPoint;

    public WarpPointManager(ConfigurationSection config)
    {
        // parse it
        for(String player : config.getKeys()) {
            Map<String, WarpPoint> temp = new HashMap<>();
            warpPoints.set(player.toLowerCase(), temp);

            List<Map<String, String>> points = config.getMapList(player);

            for(Map<String, String> map: points) {
                WarpPoint point = WarpPoint.fromConfig(map);
                temp.set(point.getName().toLowerCase(), point);
            }
        }
    }

    /**
     * finds the first warp point that matches the input
     *
     * @see    find()
     *
     * @param  sender             Player who searches for waypoints
     * @param  input              Search string
     * @param  ignorePermissions  defines if private waypoints should be hidden if access isn't granted
     * @return the first found warp point. Results owned be the sender are sorted above others
     */
    public WarpPoint findFirst(OfflinePlayer sender, String input, boolean ignorePermissions)
    {
        try {
            return find(sender, input, ignorePermissions).get(0);
        } catch( IndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Searches the list of available warp points for every point that matches
     * given input search string.
     *
     * Search string support glob style matching:
     *  "bla.*"  matches "bla.baz" and "bla.foo" but not "bla" nor "blubb"
     *  "?oo"    matches "foo" and "boo"
     *  "home.{mine,yours}" matches only "home.mine" or "home.yours"
     *
     * Additionally the input string might be prefixed by the name (or part of the name)
     * of the warp points owner. If the substring until the first dot (".") matches
     * a known players name, his warp points are searched using the remaining
     * string.
     *
     * The list is somewhat semi sorted. If no player is specified, the senders
     * own warp points are search first. After that, every other players warp
     * points are search and added to the list. This results in a list that
     * features the players own warp points - if existing - on the top of the list.
     * Warp points of other players follow.
     *
     * WARNING: the lookup is done case INSENSITIVE. That means "HoMe" machtes
     * "home" as well as "hOMe". This behavior is the same in all Methods for
     * creating and changing warp points and should therefore not cause any trouble.
     *
     * @param  sender             Player who searches for waypoints
     * @param  input              Search string
     * @param  ignorePermissions  defines if private waypoints should be hidden if access isn't granted
     */
    public List<WarpPoint> find(OfflinePlayer sender, String input, boolean ignorePermissions)
    {
        List<WarpPoint> points = null;
        String[] parts = input.split(".", 2);
        if(parts.length == 2) {
            OfflinePlayer owner = findPlayerByName(parts[0]);
            if(owner != null) {
                name = parts[1];
                points = findOwnedBy(owner, name);
            }
        }

        if(points == null) {
            points.findOwnedBy(sender, input);
            for(String plName: warpPoints.keySet()) {
                points.addAll(findOwnedBy(plName, input));
            }
        }

        if(ignorePermissions) {
            return points;
        }

        List<WarpPoint> filtered = new ArrayList<WarpPoint>;
        for(WarpPoint wp: points) {
            if(wp.isAllowed(sender)) {
                filtered.add(wp);
            }
        }
        return filtered;
    }

    /**
     * Finds a list of warp points matching the given input string that are owned
     * by the owner.
     *
     * @param owner the Owners name
     * @param input Search string. See find() for details on its format.
     */
    public List<WarpPoint> findOwnedBy(String owner, String input)
    {
        Map<String, WarpPoint> points = warpPoints.get(owner.toLowerCase());
        List<WarpPoint> found = new ArrayList<>();

        if(input.equals("*")) {
            found.addAll(points.values());
            return found;
        }

        Pattern expr = convertGlobToRegEx(input.toLowerCase());
        for(String key: points.keySet()) {
            if(expr.matcher(key).matches()) {
                found.add(points.get(key));
            }
        }

        return found;
    }

    /**
     * Finds a list of warp points matching the given input string that are owned
     * by the owner.
     *
     * @param owner the owner
     * @param input Search string. See find() for details on its format.
     */
    public List<WarpPoint> findOwnedBy(OfflinePlayer owner, String input)
    {
        return findOwnedBy(onwer.getName(), input);
    }

    public boolean create(OfflinePlayer owner, String name)
    {
        // 1 existiert schon? => return false
        // 2 existiert nicht => erstellen => in Liste => return true
    }

    public boolean delete(OfflinePlayer owner, String name)
    {
    }

    public boolean set(OfflinePlayer owner, String name)
    {
    }

    public boolean setVisibility(OfflinePlayer owner, String name, boolean visible)
    {
    }

    public boolean invite(OfflinePlayer owner, String name, Set<String> players)
    {
        List<WarpPoint> wpList = findOwnedBy(owner,name);
        Set<OfflinePlayer> invited = findPlayers(players);

        for(WarpPoint point: wpList) {
            wp.invite(invited);
        }
    }

    public boolean uninvite(OfflinePlayer owner, String name, Set<String> players)
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

    /**
     * shamelessly pirated from https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
     * thx interwebs!
     */
    private Pattern convertGlobToRegEx(String line)
    {
        line = line.trim();
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        // Remove beginning and ending * globs because they're useless
        if (line.startsWith("*")) {
            line = line.substring(1);
            strLen--;
        }
        if (line.endsWith("*")) {
            line = line.substring(0, strLen-1);
            strLen--;
        }
        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
            case '*':
                if (escaping) {
                    sb.append("\\*");
                }
                else {
                    sb.append(".*");
                }
                escaping = false;
                break;
            case '?':
                if (escaping) {
                    sb.append("\\?");
                }
                else {
                    sb.append('.');
                }
                escaping = false;
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                sb.append('\\');
                sb.append(currentChar);
                escaping = false;
                break;
            case '\\':
                if (escaping) {
                    sb.append("\\\\");
                    escaping = false;
                }
                else {
                    escaping = true;
                }
                break;
            case '{':
                if (escaping) {
                    sb.append("\\{");
                }
                else {
                    sb.append('(');
                    inCurlies++;
                }
                escaping = false;
                break;
            case '}':
                if (inCurlies > 0 && !escaping) {
                    sb.append(')');
                    inCurlies--;
                }
                else if (escaping){
                    sb.append("\\}");
                }
                else {
                    sb.append("}");
                }
                escaping = false;
                break;
            case ',':
                if (inCurlies > 0 && !escaping) {
                    sb.append('|');
                }
                else if (escaping) {
                    sb.append("\\,");
                }
                else {
                    sb.append(",");
                }
                break;
            default:
                escaping = false;
                sb.append(currentChar);
            }
        }
        return Pattern.compile(sb.toString());
    }
}
