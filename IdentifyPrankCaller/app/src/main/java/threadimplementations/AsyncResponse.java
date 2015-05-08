package threadimplementations;

import java.util.ArrayList;

import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public interface AsyncResponse
{
    void processFinish(QueryResult result);
    void processFinish(ArrayList<QueryResult> results);
}
