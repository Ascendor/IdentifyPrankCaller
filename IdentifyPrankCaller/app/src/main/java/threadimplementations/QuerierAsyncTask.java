package threadimplementations;

import android.os.AsyncTask;
import android.widget.TextView;

import java.util.ArrayList;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import hs_kl.de.identifyprankcaller.R;
import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public class QuerierAsyncTask extends AsyncTask<AbstractQuerier, Void, ArrayList<QueryResult>>
{
    private AsyncResponse delegate;
    private String phoneNumber;

    public QuerierAsyncTask(String phoneNumber, AsyncResponse delegate)
    {
        this.delegate = delegate;
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected ArrayList<QueryResult> doInBackground (AbstractQuerier... queriers)
    {
        ArrayList<QueryResult> results = new ArrayList<>();
        for (AbstractQuerier querier : queriers) {
            QueryResult result = querier.query(this.phoneNumber);
            results.add(result);
        }

        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<QueryResult> results)
    {
        delegate.processFinish(results);
    }
}
