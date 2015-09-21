package com.example.mekawy.sunrise;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #SunriseApp";
    private String Sharable_Forecast;
    private String LOG=DetailActivityFragment.class.getSimpleName();
    private final int Loader_id=1;

    private static String ForecastString;
    private ShareActionProvider mShare;

    private static final String[] FORECAST_COLUMNS = {
                            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                            WeatherContract.WeatherEntry.COLUMN_DATE,
                            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;


    public DetailActivityFragment() {

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(1,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem mItem=menu.findItem(R.id.share);
        mShare =(ShareActionProvider) MenuItemCompat.getActionProvider(mItem);

        if(ForecastString !=null)mShare.setShareIntent(createShareForecastIntent());

        else Log.i(LOG,"NO ACTION.SEND Founded");

    }

    private Intent createShareForecastIntent(){
        Intent share_intent=new Intent(Intent.ACTION_SEND);
        share_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_TEXT,ForecastString);
        return share_intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_detail, container, false);
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent Uri_intent=getActivity().getIntent();
        if(Uri_intent != null){
         return new CursorLoader(getActivity(),
                 Uri_intent.getData(),
                 FORECAST_COLUMNS,
                 null,
                 null,
                 null);

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if(!data.moveToFirst())return ;
        String date=Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String Desc=data.getString(COL_WEATHER_DESC);

        boolean metric=Utility.isMetric(getActivity());
        String high=Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), metric);
        String low=Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), metric);
        String temp=String.format("%s - %s -%s/%s", date, Desc, high, low);
        ForecastString=temp;
        TextView tc=(TextView) getView().findViewById(R.id.detail_text);
        tc.setText(temp);

        if(temp!=null){
            mShare.setShareIntent(createShareForecastIntent());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
