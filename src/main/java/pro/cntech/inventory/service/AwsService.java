package pro.cntech.inventory.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;

@Service
public class AwsService
{
    @Value("${aws.api-gateway-url}")
    private String apiURL;
    @Autowired
    private AmazonS3 s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public void uploadObject(MultipartFile multipartFile, String bucketPath, String storedFileName) throws IOException {

        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(multipartFile.getContentType());
        omd.setContentLength(multipartFile.getSize());
        omd.setHeader("filename",multipartFile.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(bucketName+bucketPath,storedFileName,multipartFile.getInputStream(),omd));
    }

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
