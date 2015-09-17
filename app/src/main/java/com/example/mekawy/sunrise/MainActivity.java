package com.example.mekawy.sunrise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FetchWeatherTask f=new FetchWeatherTask();
        f.execute("94043");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            //satrt Setting Activity
            startActivity(new Intent(getApplicationContext(),Setting_Activity.class));
            return true;
        }
        else if(id== R.id.Map_setting){
            openPreferredLocationInMap();
        }
        return super.onOptionsItemSelected(item);
    }

    //open Location form preferences in Map application which scheme is "geo"
    public void openPreferredLocationInMap(){
        SharedPreferences shrd_pref= PreferenceManager.getDefaultSharedPreferences(this);
        String current_location=shrd_pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        Uri geo_uri=Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",current_location).build();
        Intent geo_intent=new Intent(Intent.ACTION_VIEW);
        geo_intent.setData(geo_uri);

        if(geo_intent.resolveActivity(getPackageManager())!=null)
            startActivity(geo_intent);
        else Toast.makeText(getApplicationContext(),"No Such Map Application",Toast.LENGTH_SHORT).show();
    }


}
