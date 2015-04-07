package controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public class OpenCnamQuerier extends AbstractQuerier {
    private final static String uri = "https://api.opencnam.com/v2/phone/";
    @Override
    public QueryResult query (String phoneNumber)
    {
        QueryResult result = null;
        HttpClient http = new DefaultHttpClient();
        try {
            HttpResponse response = http.execute(new HttpGet(OpenCnamQuerier.uri + phoneNumber));
            InputStream content = response.getEntity().getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(content));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            result = new QueryResult(OpenCnamQuerier.uri + phoneNumber, total.toString(), total.toString());
        } catch (IOException e) {
            System.err.println("Error 1: " + e.getMessage());
        } catch (URISyntaxException e) {
            System.err.println("Error 2: " + e.getMessage());
        }
        return result;
    }
}
