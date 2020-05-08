package pro.cntech.inventory.util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MapUtil
{
    private String API_KEY = "87c48b54b7f2c7fdc3f31063f02a7fa2";
    /**

     * @Method Name : convertAddrToGPS
     * @작성일 : 2020. 2. 21.
     * @작성자 : Sangwon Hyun
     * @Method 설명 : 주소 -> GPS 변환
     * @param addr
     * @return
     * @throws Exception
     */
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

    /**

     * @Method Name : convertGPSToAddr
     * @작성일 : 2020. 2. 21.
     * @작성자 : Sangwon Hyun
     * @Method 설명 : GPS -> 주소 변환
     * @param longitude 경도 (x)
     * @param latitude 위도 (y)
     * @return
     * @throws Exception
     */
    public String convertGPSToAddr(String longitude,String latitude) throws Exception{

        final String API_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+longitude+"&y="+latitude+"&input_coord=WGS84";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","KakaoAK "+API_KEY);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> result = rest.exchange(API_URL, HttpMethod.GET, new org.springframework.http.HttpEntity(headers),String.class);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(result.getBody());
        JSONArray jsonArray = (JSONArray)jsonObject.get("documents");

        JSONObject local = (JSONObject)jsonArray.get(0);
        JSONObject jsonArray1 = (JSONObject)local.get("address");
        String localAddress = jsonArray1.get("address_name").toString();

        return localAddress;
    }

}
