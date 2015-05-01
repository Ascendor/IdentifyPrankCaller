package threadimplementations;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Alex on 17.04.2015.
 */
public class QuerierLooper extends Thread
{
    public Handler handler;

    public void run()
    {
        Looper.prepare();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        Looper.loop();
    }
}
