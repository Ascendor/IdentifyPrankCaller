package threadimplementations;

import android.app.Activity;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;

/**
 * Created by Alex on 05.05.2015.
 * Example implementation for Executor, namely ThreadPoolExecutor
 */
public class QuerierExecutor
{
    private Activity activity;
    private AsyncResponse callback;
    private ThreadPoolExecutor executor;

    public QuerierExecutor(Activity activity, AsyncResponse callback)
    {
        this.activity = activity;
        this.callback = callback;
        int n = Runtime.getRuntime().availableProcessors();
        this.executor = new ThreadPoolExecutor(n*2, n*2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

    }

    public void enqueueQuery(final String phoneNumber)
    {
        this.executor.execute(new Runnable(){
            public void run(){
                AbstractQuerier querier = new OpenCnamQuerier();
                final QueryResult result = querier.query(phoneNumber);
                Runnable uiTask = new Runnable() {
                    @Override
                    public void run() {
                        QuerierExecutor.this.callback.processFinish(result);
                    }
                };
                QuerierExecutor.this.activity.runOnUiThread(uiTask);
            }
        });
    }
}
