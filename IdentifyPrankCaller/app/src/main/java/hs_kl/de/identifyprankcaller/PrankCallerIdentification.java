package hs_kl.de.identifyprankcaller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import controller.AbstractQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;
import threadimplementations.AsyncResponse;
import threadimplementations.QuerierAsyncTask;


public class PrankCallerIdentification extends Activity implements AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prank_caller_identification);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prank_caller_identification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startSearch(View view)
    {
        QuerierAsyncTask task = new QuerierAsyncTask(this);
        task.execute("16502530000");
    }

    @Override
    public void processFinish(QueryResult result)
    {
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText(result.getDescription());
    }
}
