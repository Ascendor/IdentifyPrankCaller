package hs_kl.de.identifyprankcaller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import controller.AbstractQuerier;
import controller.GoogleQuerier;
import controller.OpenCnamQuerier;
import model.QueryResult;
import threadimplementations.AsyncResponse;
import threadimplementations.QuerierAsyncTask;
import threadimplementations.QuerierLooper;


public class PrankCallerIdentification extends Activity implements AsyncResponse {
    public  static final String LOGN = "PrankCallerIdent";
    public static final String PREFS_NAME = "PCIPrefsFile";
    private static final int MENU_SIZE = 5;
    public static final int MENU_TASK_METHOD_GUI = 0;
    public static final int MENU_TASK_METHOD_THREAD = 1;
    public static final int MENU_TASK_METHOD_ASYNC = 2;
    public static final int MENU_SEARCH_ENGINE_GOOGLE = 3;
    public static final int MENU_SEARCH_ENGINE_OPEN_CNAME = 4;

    private boolean menu_values[] = new boolean[MENU_SIZE];
    EditText edtCallingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prank_caller_identification);
        edtCallingNumber = (EditText) findViewById(R.id.edtCallingNumber);
        loadMenuValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prank_caller_identification, menu);
        restoreVisualMenuCheckState(menu);
        return true;
    }

     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            case R.id.menu_search_engine_google:
                return setVisualMenuCheckState( MENU_SEARCH_ENGINE_GOOGLE, "menu_search_engines", item );
            case R.id.menu_search_engine_open_cname:
                return setVisualMenuCheckState( MENU_SEARCH_ENGINE_OPEN_CNAME, "menu_search_engines", item );
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void startSearch(View view)
    {
        Log.i(LOGN, "startSearch(): menu_values: " + menu_values.toString());
       //Start search depending on the menu_values
       if (menu_values[MENU_TASK_METHOD_ASYNC]) {
           startSearchAsyncTask(view);
       } else if (menu_values[MENU_TASK_METHOD_GUI]){
                 startSearchGuiThread(view);
              } else if (menu_values[MENU_TASK_METHOD_THREAD]){
                        startSearchThread(view);
                     }
    }


    public String getCallingNumber(View view) {
        //returns the value of edtCallingNumber from the view
        String callingNumber = "";
        if ( edtCallingNumber != null ) {
            callingNumber = edtCallingNumber.getText().toString();
        }
        Log.i(LOGN,"getCallingNumber(): editText content: " + callingNumber);
        if (callingNumber.equals("")) callingNumber = "16502530000";
        Log.i(LOGN,"getCallingNumber(): return callingNumber: " + callingNumber);
        return callingNumber;
    }

    public void startSearchLooper (View view)
    {
        QuerierLooper looper = new QuerierLooper(this);
        looper.start();
        looper.enqueueQuery(getCallingNumber(view));
    }

    public void startSearchAsyncTask(View view)
    {
        QuerierAsyncTask task = new QuerierAsyncTask(this);
        task.execute(getCallingNumber(view));
        //task.execute("16502530000");
    }

    public void startSearchThread(View view)
    {
        final String number = getCallingNumber(view);
        new Thread(new Runnable() {
            @Override
            public void run() {
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
        }).start();
    }

    public void startSearchGuiThread(View view)
    {
        // @todo @fixme Don't do stuff like this! It's here just for demonstration purposes.
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        // End hack

        AbstractQuerier querier = new OpenCnamQuerier();
        QueryResult result = querier.query(getCallingNumber(view));
        this.processFinish(result);
    }

    @Override
    public void processFinish(QueryResult result)
    {
        TextView resultText = (TextView) findViewById(R.id.resultText);
        resultText.setText(result.getDescription());
    }



    public void loadMenuValues() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        menu_values[MENU_TASK_METHOD_GUI] = settings.getBoolean("menu_task_method_gui", false);
        menu_values[MENU_TASK_METHOD_THREAD] = settings.getBoolean("menu_task_method_thread", false);
        menu_values[MENU_TASK_METHOD_ASYNC] = settings.getBoolean("menu_task_method_async", false);
        menu_values[MENU_SEARCH_ENGINE_GOOGLE] = settings.getBoolean("menu_search_engine_google", false);
        menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME] = settings.getBoolean("menu_search_engine_open_cname", false);

        //set defaults for first use
        if (!menu_values[MENU_TASK_METHOD_ASYNC] && !menu_values[MENU_TASK_METHOD_GUI] && !menu_values[MENU_TASK_METHOD_THREAD]) {
            Log.i(LOGN, "loadMenuValues(): Set default values for MENU_TASK_METHOD");
            menu_values[MENU_TASK_METHOD_GUI] = true;
        }
        if (!menu_values[MENU_SEARCH_ENGINE_GOOGLE] && !menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]) {
            Log.i(LOGN, "loadMenuValues(): Set default values for MENU_SEARCH_ENGINE");
            menu_values[MENU_SEARCH_ENGINE_GOOGLE] = true;
        }
    }


    public void saveMenuValues() {
        //persist in shared preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("menu_task_method_gui", menu_values[MENU_TASK_METHOD_GUI]);
        editor.putBoolean("menu_task_method_thread", menu_values[MENU_TASK_METHOD_THREAD]);
        editor.putBoolean("menu_task_method_async", menu_values[MENU_TASK_METHOD_ASYNC]);
        editor.putBoolean("menu_search_engine_google", menu_values[MENU_SEARCH_ENGINE_GOOGLE]);
        editor.putBoolean("menu_search_engine_open_cname", menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]);
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
        }
        if (menuGroup.equals("menu_search_engines")){
            menu_values[MENU_SEARCH_ENGINE_GOOGLE] = false;
            menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME] = false;
        }
        menu_values[ menuId ] = true;

        Log.i(LOGN, "setVisualMenuCheckState(): menu_values: " + menu_values.toString());
        //persist menu values
        saveMenuValues();

        return true;
    }


    public void restoreVisualMenuCheckState(Menu menu) {
        //restore check state values
        menu.findItem(R.id.menu_task_method_gui).setChecked(menu_values[MENU_TASK_METHOD_GUI]);
        menu.findItem(R.id.menu_task_method_thread).setChecked(menu_values[MENU_TASK_METHOD_THREAD]);
        menu.findItem(R.id.menu_task_method_async).setChecked(menu_values[MENU_TASK_METHOD_ASYNC]);
        menu.findItem(R.id.menu_search_engine_google).setChecked(menu_values[MENU_SEARCH_ENGINE_GOOGLE]);
        menu.findItem(R.id.menu_search_engine_open_cname).setChecked(menu_values[MENU_SEARCH_ENGINE_OPEN_CNAME]);
    }

}
