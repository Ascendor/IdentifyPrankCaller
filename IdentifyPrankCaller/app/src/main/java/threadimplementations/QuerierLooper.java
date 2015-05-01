package threadimplementations;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;

/**
 * Created by Alex on 17.04.2015.
 */
public class QuerierLooper extends Thread
{
    public Handler handler;

    public void run()
    {
        Looper.prepare();
     /*
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String number = bundle.getString("number");
                AbstractQuerier querier = new OpenCnamQuerier();
                final QueryResult result = querier.query(number);
                PrankCallerIdentification.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run()
                    {
                        PrankCallerIdentification.this.processFinish(result);
                    }
                });
            }
        };
        */
        Looper.loop();
    }
}
