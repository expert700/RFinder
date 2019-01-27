package rfinder.Tasks;

import javafx.concurrent.Task;
import rfinder.Hazeron.Galaxy;
import rfinder.Hazeron.Sector;
import rfinder.Hazeron.System;
import rfinder.Hazeron.Zone;

import java.util.ArrayList;

public class ZoneFilterTask extends Task<ArrayList<Zone>> {
    private final ArrayList<Zone> zones;
    private final Galaxy galaxy;
    private final Sector sector;
    private final System system;
    private final String range;

    public ZoneFilterTask(ArrayList<Zone> zones, Galaxy galaxy, Sector sector, System system, String range) {
        this.zones = zones;
        this.galaxy = galaxy;
        this.sector = sector;
        this.system = system;
        this.range = range;
    }

    private boolean checkRange(System target, int parsecs) {
        if (target.getParent().getParent() != galaxy) return false;

        double originX, originY, originZ;
        originX = originY = originZ = 0;

        boolean set = false;
        if ((system == null || system.isPlaceholder())
                && (sector != null && !sector.isPlaceholder())) {
            originX = sector.getX() * 10;
            originY = sector.getY() * 10;
            originZ = sector.getZ() * 10;
            set = true;
        } else if ((system != null && !system.isPlaceholder())) {
            originX = system.getX();
            originY = system.getY();
            originZ = system.getZ();
            set = true;
        }

        if (set) {
            double targetX = target.getX();
            double targetY = target.getY();
            double targetZ = target.getZ();

            double dist = Math.sqrt((originX - targetX) * (originX - targetX) +
                    (originY - targetY) * (originY - targetY) + (originZ - targetZ) * (originZ - targetZ));
            return dist <= parsecs;
        } else {
            return false;
        }
    }

    @Override
    protected ArrayList<Zone> call() {
        ArrayList<Zone> matches = new ArrayList<>();

        for (Zone z : zones) {
            boolean galaxyMatch = (galaxy == null || galaxy.isPlaceholder()) || galaxy == z.getGalaxy();

            boolean sectorMatch = (sector == null || sector.isPlaceholder()) || sector == z.getSector();

            boolean systemMatch = (system == null || system.isPlaceholder()) || system == z.getSystem();

            int parsecs = range.isEmpty() ? 0 : Integer.parseInt(range);
            boolean rangeMatch = checkRange(z.getSystem(), parsecs);

            if ((galaxyMatch && sectorMatch && systemMatch) || rangeMatch) {
                matches.add(z);
            }
        }

        return matches;
    }
}
