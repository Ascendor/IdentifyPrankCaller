package controller;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import model.QueryResult;

/**
 * Created by aschoecke on 07.05.2015.
 */
public class MockQuerier extends AbstractQuerier
{
    private final static String uri = "https://api.opencnam.com/v2/phone/";

    public QueryResult query(String phoneNumber)
    {
        QueryResult dummyResult = null;
        try {
            dummyResult = new QueryResult(uri, "Foo Bar", "Fucked up beyond all recognition");
        } catch (URISyntaxException e) {
            // no action
        }
        return dummyResult;
    }
}
