package controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 * GoogleQuerier looks up a phone number with Google
 */
public class GoogleQuerier extends AbstractQuerier {
    private final static String uri = "https://www.googleapis.com/customsearch/v1";
    @Override
    public QueryResult query (String phoneNumber)
    {
        QueryResult result = null;
        HttpClient http = new DefaultHttpClient();
        try {
            HttpResponse response = http.execute(new HttpGet(GoogleQuerier.uri + "?q=" + phoneNumber));
            InputStream content = response.getEntity().getContent();
            result = new QueryResult(GoogleQuerier.uri, content.toString(), content.toString());
        } catch (IOException e) {
            System.err.println("Error 1: " + e.getMessage());
        } catch (URISyntaxException e) {
            System.err.println("Error 2: " + e.getMessage());
        }
        return result;
    }
}
