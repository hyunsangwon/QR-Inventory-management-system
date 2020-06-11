package pro.cntech.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.cntech.inventory.service.StatusService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class InventoryApplicationTests
{
  	//http://www.geodatasource.com/developers/ 참고
	@Test
	void contextLoads()
	{
		// 킬로미터(Kilo Meter) 단위
		int distanceKiloMeter =
				(int)distance(36.4324528, 127.3944195, 36.343869459442, 127.448909230153, "meter");
		System.out.println("distance ====> "+distanceKiloMeter);
 	}

 	public double distance(double gpsLat, double gpsLon, double companyLat, double companyLon, String unit)
	{
		double theta = gpsLon - companyLon;
		double dist = Math.sin(deg2rad(gpsLat)) * Math.sin(deg2rad(companyLat)) + Math.cos(deg2rad(gpsLat)) * Math.cos(deg2rad(companyLat)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		if (unit == "kilometer") {
			dist = dist * 1.609344;
		} else if(unit == "meter"){
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
