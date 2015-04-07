package threadimplementations;

import android.os.AsyncTask;
import android.widget.TextView;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import hs_kl.de.identifyprankcaller.R;
import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public class QuerierAsyncTask extends AsyncTask<String, Void, QueryResult>
{
    private AsyncResponse delegate;

    public QuerierAsyncTask(AsyncResponse delegate)
    {
        this.delegate = delegate;
    }

    @Override
    protected QueryResult doInBackground (String... urls)
    {
        AbstractQuerier querier = new OpenCnamQuerier();
        QueryResult result = querier.query(urls[0]);
        return result;
    }

    @Override
    protected void onPostExecute(QueryResult result)
    {
        delegate.processFinish(result);
    }
}
