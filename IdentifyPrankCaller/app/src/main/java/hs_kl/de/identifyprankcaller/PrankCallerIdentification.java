package hs_kl.de.identifyprankcaller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import controller.AbstractQuerier;
import controller.GoogleQuerier;
import controller.MockQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;
import threadimplementations.AsyncResponse;
import threadimplementations.QuerierAsyncTask;
import threadimplementations.QuerierExecutor;
import threadimplementations.QuerierLooper;


public class PrankCallerIdentification extends Activity implements AsyncResponse {
    public  static final String LOGN = "PrankCallerIdent";
    public static final String PREFS_NAME = "PCIPrefsFile";
    private static final int MENU_SIZE = 8;
    public static final int MENU_TASK_METHOD_GUI = 0;
    public static final int MENU_TASK_METHOD_THREAD = 1;
    public static final int MENU_TASK_METHOD_ASYNC = 2;
    public static final int MENU_TASK_METHOD_LOOPER = 3;
    public static final int MENU_TASK_METHOD_EXECUTOR = 4;
    public static final int MENU_SEARCH_ENGINE_GOOGLE = 5;
    public static final int MENU_SEARCH_ENGINE_OPEN_CNAME = 6;
    public static final int MENU_SEARCH_ENGINE_MOCK = 7;

    private boolean menu_values[] = new boolean[MENU_SIZE];
    EditText edtCallingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prank_caller_identification);
        edtCallingNumber = (EditText) findViewById(R.id.edtCallingNumber);
        loadMenuValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prank_caller_identification, menu);
        restoreVisualMenuCheckState(menu);
        return true;
    }

     @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int iid = item.getItemId();

         //Set checked state to the selected menu item
        switch (iid) {
            case R.id.menu_task_method_gui:
                return setVisualMenuCheckState( MENU_TASK_METHOD_GUI, "menu_task_methods", item );
            case R.id.menu_task_method_thread:
                return setVisualMenuCheckState( MENU_TASK_METHOD_THREAD, "menu_task_methods", item );
            case R.id.menu_task_method_async:
                return setVisualMenuCheckState( MENU_TASK_METHOD_ASYNC, "menu_task_methods", item );
            case R.id.menu_task_method_looper:
                return setVisualMenuCheckState( MENU_TASK_METHOD_LOOPER, "menu_task_methods", item );
            case R.id.menu_task_method_executor:
                return setVisualMenuCheckState( MENU_TASK_METHOD_EXECUTOR, "menu_task_methods", item );
            case R.id.menu_search_engine_google:
                return setVisualMenuCheckState( MENU_SEARCH_ENGINE_GOOGLE, "menu_search_engines", item );
            case R.id.menu_search_engine_open_cname:
                return setVisualMenuCheckState( MENU_SEARCH_ENGINE_OPEN_CNAME, "menu_search_engines", item );
            case R.id.menu_search_engine_mock:
                return setVisualMenuCheckState( MENU_SEARCH_ENGINE_MOCK, "menu_search_engines", item );
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void startSearch(View view)
    {
        this.resetResult();
        Log.i(LOGN, "startSearch(): menu_values: " + Arrays.toString(menu_values));

       //Start search depending on the menu_values
        if (menu_values[MENU_TASK_METHOD_ASYNC]) {
            startSearchAsyncTask(view, this.getQueriers());
        } else if (menu_values[MENU_TASK_METHOD_GUI]){
            startSearchGuiThread(view, this.getQueriers());
        } else if (menu_values[MENU_TASK_METHOD_THREAD]){
             startSearchThread(view, this.getQueriers());
        } else if (menu_values[MENU_TASK_METHOD_LOOPER]){
            startSearchLooper(view, this.getQueriers());
        } else if (menu_values[MENU_TASK_METHOD_EXECUTOR]) {
            startSearchExecutor(view, this.getQueriers());
        }
    }

    public void startSearchGuiThread(View view)
    {
        this.resetResult();
        List<AbstractQuerier> queriers = new ArrayList<>();
        AbstractQuerier querier = new MockQuerier();
        queriers.add(querier);
        this.startSearchGuiThread(view, queriers);
    }

    public void startSearchGuiThread(View view, List<AbstractQuerier> queriers)
    {
        // @todo @fixme Don't do stuff like this! It's here just for demonstration purposes.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // End hack
        for (AbstractQuerier querier : queriers) {
            this.processFinish(querier.query(getCallingNumber(view)));
        }
    }

    public void startSearchThread (View view)
    {
        this.resetResult();
        List<AbstractQuerier> list = new ArrayList<>();
        list.add(new MockQuerier());
        this.startSearchThread(view, list);
    }

    public void startSearchThread(View view, List<AbstractQuerier> queriers)
    {
        final String number = getCallingNumber(view);
        for (final AbstractQuerier querier : queriers) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final QueryResult result = querier.query(number);
                    PrankCallerIdentification.this.runOnUiThread(new Runnable(){
                        @Override
                        public void run()
                        {
                            PrankCallerIdentification.this.processFinish(result);
                        }
                    });
                }
            }).start();
        }
    }

    public void startSearchLooper (View view)
    {
        this.resetResult();
        List<AbstractQuerier> list = new ArrayList<>();
        list.add(new MockQuerier());
        this.startSearchLooper(view, list);
    }

    public void startSearchLooper (View view, List<AbstractQuerier> queriers)
    {
        QuerierLooper looper = new QuerierLooper(this);
        for (final AbstractQuerier querier : queriers) {
            looper.start();
            looper.enqueueQuery(querier, getCallingNumber(view));
        }
    }

    public void startSearchAsyncTask(View view)
    {
        this.resetResult();
        AbstractQuerier querier = new MockQuerier();
        ArrayList<AbstractQuerier> queriers = new ArrayList<>();
        queriers.add(querier);
        this.startSearchAsyncTask(view, queriers);
    }

    public void startSearchAsyncTask (View view, List<AbstractQuerier> queriers)
    {
        for (final AbstractQuerier querier : queriers) {
            QuerierAsyncTask task = new QuerierAsyncTask(getCallingNumber(view), this);
            task.execute(querier);
        }
    }

    public void startSearchExecutor(View view)
    {
        this.resetResult();
        List<AbstractQuerier> queriers = new ArrayList<>();
        AbstractQuerier querier = new MockQuerier();
        queriers.add(querier);

        this.startSearchExecutor(view, queriers);
    }

    public void startSearchExecutor(View view, List<AbstractQuerier> queriers)
    {
        QuerierExecutor executor = new QuerierExecutor(this, this);
        for (AbstractQuerier querier : queriers) {
            executor.enqueueQuery(getCallingNumber(view), querier);
        }
    }

    public void resetResult()
    {
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText("");
    }

    public String getCallingNumber(View view) {
        //returns the value of edtCallingNumber from the view
        String callingNumber = "";
        if ( edtCallingNumber != null ) {
            callingNumber = edtCallingNumber.getText().toString();
        }
        Log.i(LOGN, "getCallingNumber(): editText content: " + callingNumber);
        if (callingNumber.equals("")) callingNumber = "16502530000";
        Log.i(LOGN, "getCallingNumber(): return callingNumber: " + callingNumber);
        return callingNumber;
    }

    public List<AbstractQuerier> getQueriers()
    {
        ArrayList<AbstractQuerier> queriers = new ArrayList<>();
        if (menu_values[MENU_SEARCH_ENGINE_GOOGLE]) {
            AbstractQuerier googleQuerier = new GoogleQuerier();
            queriers.add(googleQuerier);
        }

        if (menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]) {
            AbstractQuerier cnamQuerier = new OpenCnamQuerier();
            queriers.add(cnamQuerier);


        if (menu_values[MENU_SEARCH_ENGINE_MOCK]) {
            AbstractQuerier mockQuerier = new MockQuerier();
            queriers.add(mockQuerier);
        }   }
        return queriers;
    }

    @Override
    public void processFinish(QueryResult result)
    {
        TextView resultText = (TextView) findViewById(R.id.resultText);
        //resultText.append(result.getUri() + "\n | " + result.getShortDescription() + ": " + result.getDescription() + "<![CDATA[<br>]]>" + System.getProperty ("line.separator"));
        resultText.setText(result.getDescription());
    }

    @Override
    public void processFinish(ArrayList<QueryResult> results)
    {
        for (QueryResult result : results) {
            TextView resultText = (TextView) findViewById(R.id.resultText);
            resultText.append(result.getDescription());
        }

    }


    public void loadMenuValues() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        menu_values[MENU_TASK_METHOD_GUI] = settings.getBoolean("menu_task_method_gui", false);
        menu_values[MENU_TASK_METHOD_THREAD] = settings.getBoolean("menu_task_method_thread", false);
        menu_values[MENU_TASK_METHOD_ASYNC] = settings.getBoolean("menu_task_method_async", false);
        menu_values[MENU_TASK_METHOD_LOOPER] = settings.getBoolean("menu_task_method_looper", false);
        menu_values[MENU_TASK_METHOD_EXECUTOR] = settings.getBoolean("menu_task_method_executor", false);
        menu_values[MENU_SEARCH_ENGINE_GOOGLE] = settings.getBoolean("menu_search_engine_google", false);
        menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME] = settings.getBoolean("menu_search_engine_open_cname", false);
        menu_values[MENU_SEARCH_ENGINE_MOCK] = settings.getBoolean("menu_search_engine_mock", false);


        //set defaults for first use
        if (!menu_values[MENU_TASK_METHOD_ASYNC] && !menu_values[MENU_TASK_METHOD_GUI] &&
            !menu_values[MENU_TASK_METHOD_THREAD] && !menu_values[MENU_TASK_METHOD_LOOPER] &&
                !menu_values[MENU_TASK_METHOD_EXECUTOR]
        ) {
            Log.i(LOGN, "loadMenuValues(): Set default values for MENU_TASK_METHOD");
            menu_values[MENU_TASK_METHOD_GUI] = true;
        }
        if (!menu_values[MENU_SEARCH_ENGINE_GOOGLE] && !menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]
                && !menu_values[MENU_SEARCH_ENGINE_MOCK]) {
            Log.i(LOGN, "loadMenuValues(): Set default values for MENU_SEARCH_ENGINE");
            menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME] = true;
        }
    }


    public void saveMenuValues() {
        //persist in shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("menu_task_method_gui", menu_values[MENU_TASK_METHOD_GUI]);
        editor.putBoolean("menu_task_method_thread", menu_values[MENU_TASK_METHOD_THREAD]);
        editor.putBoolean("menu_task_method_async", menu_values[MENU_TASK_METHOD_ASYNC]);
        editor.putBoolean("menu_task_method_looper", menu_values[MENU_TASK_METHOD_LOOPER]);
        editor.putBoolean("menu_task_method_executor", menu_values[MENU_TASK_METHOD_EXECUTOR]);
        editor.putBoolean("menu_search_engine_google", menu_values[MENU_SEARCH_ENGINE_GOOGLE]);
        editor.putBoolean("menu_search_engine_open_cname", menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]);
        editor.putBoolean("menu_search_engine_mock", menu_values[MENU_SEARCH_ENGINE_MOCK]);
        editor.apply();
        Log.i(LOGN, "saveMenuValues(): Menu values persisted");
    }

    //handles all jobs to set a menu item check state
    public boolean setVisualMenuCheckState( int menuId, String menuGroup, MenuItem item ) {
        //set checked state visual
        item.setChecked(!item.isChecked());
        //unset all item values in group and set clicked one
        //TODO: Very dirty coded!! Do it a better way!
        if (menuGroup.equals("menu_task_methods")){
            menu_values[MENU_TASK_METHOD_GUI] = false;
            menu_values[MENU_TASK_METHOD_THREAD] = false;
            menu_values[MENU_TASK_METHOD_ASYNC] = false;
            menu_values[MENU_TASK_METHOD_LOOPER] = false;
            menu_values[MENU_TASK_METHOD_EXECUTOR] = false;
            menu_values[ menuId ] = true;
        }
        if (menuGroup.equals("menu_search_engines")){
            if (menu_values[menuId]) {
                menu_values[menuId] = false;
            } else {
                menu_values[menuId] = true;
            }
        }

        Log.i(LOGN, "setVisualMenuCheckState(): menu_values: " +Arrays.toString(menu_values));
        //persist menu values
        saveMenuValues();

        return true;
    }


    public void restoreVisualMenuCheckState(Menu menu) {
        //restore check state values
        menu.findItem(R.id.menu_task_method_gui).setChecked(menu_values[MENU_TASK_METHOD_GUI]);
        menu.findItem(R.id.menu_task_method_thread).setChecked(menu_values[MENU_TASK_METHOD_THREAD]);
        menu.findItem(R.id.menu_task_method_async).setChecked(menu_values[MENU_TASK_METHOD_ASYNC]);
        menu.findItem(R.id.menu_task_method_looper).setChecked(menu_values[MENU_TASK_METHOD_LOOPER]);
        menu.findItem(R.id.menu_task_method_executor).setChecked(menu_values[MENU_TASK_METHOD_EXECUTOR]);
        menu.findItem(R.id.menu_search_engine_google).setChecked(menu_values[MENU_SEARCH_ENGINE_GOOGLE]);
        menu.findItem(R.id.menu_search_engine_open_cname).setChecked(menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]);
        menu.findItem(R.id.menu_search_engine_mock).setChecked(menu_values[MENU_SEARCH_ENGINE_MOCK]);
    }

}
