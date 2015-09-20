package com.example.mekawy.sunrise;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Json_parser extends AsyncTask<Object,Void,String[]>{

    private ArrayAdapter<String> Json_ArrayAdapter;
    private Context Json_context;

    public Json_parser(ArrayAdapter<String> Origin_ArrayAdapter,Context Origin_Context){
            Json_ArrayAdapter=Origin_ArrayAdapter;
            Json_context=Origin_Context;
    }



    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        //unsuitable method
        Json_ArrayAdapter.clear();
        for(String entry:strings){
            Json_ArrayAdapter.add(entry);
        }
    }

    //Append High and Low tempreature Format Like: 26/10
    private String HighLowFormat(double High,double Low){
        Long High_t=Math.round(High);
        Long Low_t=Math.round(Low);
        return High_t+" / "+Low_t;
    }


    // Create String[] and Call Function "getReadableDateString" with "x" Days_no for number of days Req.
    private String[] getReadableDateString(int Days_no){
        int index;
        SimpleDateFormat mFormat=new SimpleDateFormat("EEE MMM dd");
        Calendar mCalendar=new GregorianCalendar();
        String[] Days_Strings=new String[Days_no];
        for(index=0;index<Days_no;index++){
            Days_Strings[index]=mFormat.format(mCalendar.getTime());
            mCalendar.add(mCalendar.DAY_OF_MONTH, 1);
        }
        return Days_Strings;
    }



    long addLocation(String location_Setting ,String City_name,double lat,double lon ){
            long LocationID;

        Cursor LocatonCursor=Json_context.getContentResolver().
                query(WeatherContract.LocationEntry.CONTENT_URI,
                        new String[]{WeatherContract.LocationEntry._ID},
                        WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING +" = ?",
                        new String[]{location_Setting},
                        null
                );

        if(LocatonCursor.moveToFirst()){
        int LocationIndex=LocatonCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
        LocationID=LocatonCursor.getLong(LocationIndex);
        }

        else {
            ContentValues locationValues=new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, location_Setting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, City_name);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

           Uri insert_uri=Json_context.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,locationValues);
            Log.i("retUri",insert_uri.toString());
            LocationID= ContentUris.parseId(insert_uri);
        }

        LocatonCursor.close();
        return LocationID;
    }



    //equal to method
    @Override
    protected String[] doInBackground(Object... mEntry){

        String json_text_entry=(String) mEntry[0];
        int Day_count_entry=(Integer) mEntry[1];


        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";



        String[] resultStrs = new String[Day_count_entry];
        resultStrs=getReadableDateString(Day_count_entry);

        try {
            JSONObject forecastJson = new JSONObject(json_text_entry);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            for(int i=0;i<weatherArray.length();i++){

                double m_min_temp,m_max_temp;
                String m_temp_result;

                JSONObject dayForecast = weatherArray.getJSONObject(i);
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

                //Adding Description Part
                resultStrs[i]+=" - "+weatherObject.getString(OWM_DESCRIPTION);

                //Adding Temp part
                m_min_temp=temperatureObject.getDouble(OWM_MIN);
                m_max_temp=temperatureObject.getDouble(OWM_MAX);
                m_temp_result=HighLowFormat(m_max_temp, m_min_temp);
                resultStrs[i]+=" - "+m_temp_result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addLocation("548","BAHAMA",26.3,20);

        return resultStrs;
    }
}
