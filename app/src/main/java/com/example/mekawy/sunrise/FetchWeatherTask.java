package com.example.mekawy.sunrise;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchWeatherTask extends AsyncTask<String,Void,String>{

    private String LOG_TAG=FetchWeatherTask.class.getSimpleName()+"TAG";
    private int numDays = 7;
    private String Location_setting;



    private ArrayAdapter<String> mFetchAdapter;
    private Context mFetchContext;

    public FetchWeatherTask(ArrayAdapter<String> F_Adapter,Context F_Context){
            mFetchAdapter=F_Adapter;
            mFetchContext=F_Context;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //check results
        Log.i(LOG_TAG,s);
        Json_parser parser=new Json_parser(mFetchAdapter,mFetchContext);
        //execute Json_parser Asynch Task using object contain "Json_text" and number of days;
        parser.execute(s,Location_setting,numDays);
    }

    @Override
    protected String doInBackground(String... strings) {
    if(strings.length==0) return null;
        Location_setting=strings[0];
        HttpURLConnection urlConnection=null;
        BufferedReader reader=null;
        String forecastJsonStr=null;

        //Base URL
        final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

        //URI parameters
        String format = "json";
        String units = "metric";

        //URI prefix
        final String QUERY_PARAM = "q";
        final String FORMAT_PARAM = "mode";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, strings[0])
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();
//        Query sample -- http://api.openweathermap.org/data/2.5/forecast/daily?q={location_id}&mode=json&units=metric&cnt=7.
//        Log.d(LOG_TAG,builtUri.toString());

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //InputReader
            InputStream inputStream=urlConnection.getInputStream();
            if(inputStream==null)return null;
            //Buffered Reader
            reader=new BufferedReader(new InputStreamReader(inputStream));
            //cashed Strings
            StringBuffer buffer=new StringBuffer();
            String Line;
            while((Line=reader.readLine())!=null) buffer.append(Line+"\n");
            //check if buffer existed but not valued
            if(buffer.length()==0) return null;
            return  buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
