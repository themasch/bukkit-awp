package it.masch.bukkit.awp;

import org.bukkit.Server;
import org.bukkit.Location;

class WarpPoint extends Location 
{
    public WarpPoint(Location loc) 
    {
        super(loc.getWorld(), loc.getX(), loc.getY(), loc.getY(), loc.getYaw(), loc.getPitch());
    }

    public WarpPoint(Server srv, String str) throws Exception
    {
        super(null, 0, 0, 0, 0, 0);
        String[] params = str.split(",");
        if(params.length != 6) {
            throw new Exception("invalid data format");
        }
        Location loc = new Location(
                srv.getWorld(params[0]),
                Double.parseDouble(params[1]),  // X 
                Double.parseDouble(params[2]),  // Y
                Double.parseDouble(params[3]),  // Z
                Float.valueOf(params[4]).floatValue(),    // yaw
                Float.valueOf(params[5]).floatValue()     // pitch
        );
        this.setWorld(loc.getWorld());
        this.add(loc);
        this.setYaw(loc.getYaw());
        this.setPitch(loc.getPitch());
    }

    public String toString()
    {
        return "" + this.getWorld().getName() + "," + 
                    this.getX() + "," + 
                    this.getY() + "," + 
                    this.getZ() + "," + 
                    this.getYaw() + "," + 
                    this.getPitch(); 
    }
}
