package model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Alex on 07.04.2015.
 */
public class QueryResult
{
    private URI uri;
    private String shortDescription;
    private String description;

    public QueryResult(String uri, String shortDescription, String description) throws URISyntaxException {
        this.uri = new URI(uri);
        this.shortDescription = shortDescription;
        this.description = description;
    }
    public URI getUri()
    {
        return this.uri;

    }

    public String getShortDescription()
    {
        return this.shortDescription;
    }

    public String getDescription()
    {
        return this.description;
    }
}
