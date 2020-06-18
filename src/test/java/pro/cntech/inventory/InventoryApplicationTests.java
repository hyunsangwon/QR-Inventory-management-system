package pro.cntech.inventory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pro.cntech.inventory.service.StatusService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class InventoryApplicationTests
{
  	//http://www.geodatasource.com/developers/ 참고
	private String API_KEY = "87c48b54b7f2c7fdc3f31063f02a7fa2";

	@Test
	void contextLoads() throws Exception
	{
		// 킬로미터(Kilo Meter) 단위
		/*int distanceKiloMeter =
				(int)distance(36.4324528, 127.3944195, 36.343869459442, 127.448909230153, "meter");
		System.out.println("distance ====> "+distanceKiloMeter);*/
		String gps = convertAddrToGPS("서울특별시 송파구 위례성대로 6");
		System.out.println("GPS =======> "+gps);
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

	public String convertAddrToGPS(String addr) throws Exception
	{

		final String API_URL = "https://dapi.kakao.com/v2/local/search/address.json?query="+addr;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization","KakaoAK "+API_KEY);

		RestTemplate rest = new RestTemplate();
		ResponseEntity<String> result = rest.exchange(API_URL, HttpMethod.GET, new org.springframework.http.HttpEntity(headers),String.class);

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(result.getBody());
		JSONArray jsonArray = (JSONArray)jsonObject.get("documents");

		JSONObject local = (JSONObject)jsonArray.get(0);
		JSONObject jsonArray1 = (JSONObject)local.get("address");

		String longitude = jsonArray1.get("x").toString();//경도 127.
		String latitude = jsonArray1.get("y").toString();//위도 36.

		return longitude+"/"+latitude;
	}


}
