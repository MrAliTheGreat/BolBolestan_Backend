package ExternalServicesHandlers;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.ArrayList;

public class importHandler {

    public static String getResponseFromService(String address){
        CloseableHttpResponse httpResponse;
        String response;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(address);
        try{
            httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getEntity() != null){
                response = EntityUtils.toString(httpResponse.getEntity());
            }else{
                response = null;
            }
        }catch (IOException err){
            response = err.getMessage();
        };

        return response;
    }

    public static JSONArray importFromAddress(String address){
        return (JSONArray) JSONValue.parse(getResponseFromService(address));
    }

    public static int getStatusCodeFromServer(String address){
        CloseableHttpResponse httpResponse;
        int response;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(address);
        try{
            httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getEntity() != null){
                response = httpResponse.getStatusLine().getStatusCode();
            }else{
                response = -1;
            }
        }catch (IOException err){
            response = -2;
        };

        return response;
    }

    public static String getPostResponseFromService(String address){
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(address);
        CloseableHttpResponse serverResponse;
        String resultString;

        try {
            serverResponse = client.execute(httpPost);
            resultString = serverResponse.getFirstHeader("Location").getValue();
        }catch (IOException err){
            resultString = err.getMessage();
        }
        return resultString;
    }

}
