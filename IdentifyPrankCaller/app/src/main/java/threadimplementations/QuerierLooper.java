package threadimplementations;

import android.os.Handler;
import android.os.Looper;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;

/**
 * Created by Alex on 17.04.2015.
 */
public class QuerierLooper extends Thread
{
    public Handler handler;
    private AsyncResponse callback;

    public QuerierLooper(AsyncResponse response)
    {
        super();
        this.callback = response;

    }

    public void run()
    {
        Looper.prepare();

        handler = new Handler();

        Looper.loop();
    }

    public synchronized void enqueueQuery(final String phoneNumber) {
        this.handler.post(new Runnable(){
            public void run(){
                AbstractQuerier querier = new OpenCnamQuerier();
                final QueryResult result = querier.query(phoneNumber);
                Runnable uiTask = new Runnable() {
                    @Override
                    public void run() {
                        QuerierLooper.this.callback.processFinish(result);
                    }
                };
                new Handler(Looper.getMainLooper()).post(uiTask);
            }
        });
    }
}
