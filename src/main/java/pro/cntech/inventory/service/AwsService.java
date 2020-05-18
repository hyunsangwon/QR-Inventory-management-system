package pro.cntech.inventory.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

@Service
public class AwsService
{
    @Value("${aws.api-gateway-url}")
    private String apiURL;

    /* AWS API Gateway 연결 */
    public String getConvertedText(String imgNo) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiURL + "?imgNo=" + imgNo);
        String convertedTextResult = null;
        String dynamodbFiledName = "maxText";
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                JsonReader jsonReader = Json.createReader(new StringReader(result));
                JsonObject jsonObject = jsonReader.readObject();
                jsonReader.close();
                jsonObject.get("body");
                JsonArray arr = (JsonArray) jsonObject.get("body");
                if(arr.size() != 0) {
                    JsonObject convertedText = (JsonObject) arr.get(0);
                    convertedTextResult = convertedText.get(dynamodbFiledName).toString();
                }else {
                    return "인식 실패";
                }
            }else {
                return "인식 실패";
            }
        }finally {
            httpClient.close();
        }

        return convertedTextResult;
    }
}
