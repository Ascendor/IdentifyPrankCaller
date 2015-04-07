package threadimplementations;

import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public interface AsyncResponse
{
    void processFinish(QueryResult result);
}
