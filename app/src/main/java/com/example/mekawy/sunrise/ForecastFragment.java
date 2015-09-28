package com.example.mekawy.sunrise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;



public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    //many Restrections and will be modified later
    private ForecastAdapter mForecastAdapter;
    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                        WeatherContract.WeatherEntry.COLUMN_DATE,
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                        WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                        WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                        WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };



    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;




    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER,null,this);
    }

    public void updateWeather(){
        String loc=Utility.getPreferredLocation(getActivity());
        FetchWeatherTask new_weather_fetch=new FetchWeatherTask(getActivity());
        new_weather_fetch.execute(loc);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        mForecastAdapter=new ForecastAdapter(getActivity(),cur,0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur=(Cursor) adapterView.getItemAtPosition(i);

                if(cur !=null){
                    String Location_settings=Utility.getPreferredLocation(getActivity());

                    Intent i_intent=new Intent(getActivity(),DetailActivity.class).
                         setData(WeatherContract.WeatherEntry.
                         buildWeatherLocationWithDate(Location_settings,cur.getLong(COL_WEATHER_DATE)));

                    startActivity(i_intent);
                }
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("mywatcher","oncreateLoader");

        String locationSetting=Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),weatherForLocationUri,FORECAST_COLUMNS,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mForecastAdapter.swapCursor(null);
    }
}
