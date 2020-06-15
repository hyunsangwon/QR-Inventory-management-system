package pro.cntech.inventory.util;

public class DistanceCalculation
{

    public double distance(double gpsLat, double gpsLon, double companyLat, double companyLon, String unit)
    {
        double theta = gpsLon - companyLon;
        double dist = Math.sin(deg2rad(gpsLat)) * Math.sin(deg2rad(companyLat)) + Math.cos(deg2rad(gpsLat)) * Math.cos(deg2rad(companyLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer")
        {
            dist = dist * 1.609344;
        }
        else if(unit == "meter")
        {
            dist = dist * 1609.344;
        }
        return Math.round(dist);
    }

    public double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }
}
