package com.example.mekawy.sunrise;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;


public class Json_parser extends AsyncTask<Object,Void,String[]>{

    private final String LOG_TAG = Json_parser.class.getSimpleName();

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
        if(strings.length>0) {
            Json_ArrayAdapter.clear();
            for (String entry : strings) {
                Json_ArrayAdapter.add(entry);
            }
        }

    }


    //Append High and Low tempreature Format Like: 26/10
    private String HighLowFormat(double High,double Low){
        Long High_t=Math.round(High);
        Long Low_t=Math.round(Low);
        return High_t+" / "+Low_t;
    }

    //Replace with origin sake code
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }



    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = HighLowFormat(
                    weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP),
                    weatherValues.getAsDouble(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
            resultStrs[i] = getReadableDateString(
                    weatherValues.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)) +
                    " - " + weatherValues.getAsString(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
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


    private String[] getWeatherDataFromJson(String forecastJsonStr, String locationSetting) throws JSONException{

        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            //Get location Table Data First
            JSONObject City_object=forecastJson.getJSONObject(OWM_CITY);
            String City_name=City_object.getString(OWM_CITY_NAME);

            JSONObject Coord=City_object.optJSONObject(OWM_COORD);
            double lon=Coord.getDouble(OWM_LONGITUDE);
            double lat=Coord.getDouble(OWM_LATITUDE);

            long mLocation_id=addLocation(locationSetting,City_name,lat,lon);
//            Log.i(LOG_TAG,Long.toString(mLocation_id));

            Vector<ContentValues> cVVector = new Vector<ContentValues>(weatherArray.length());


            Time dayTime=new Time();
            dayTime.setToNow();


            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);



            for (int i=0;i<weatherArray.length();i++){

                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;


                JSONObject dayforecast =weatherArray.getJSONObject(i);
                dateTime=dayTime.setJulianDay(julianStartDay + i);

                pressure=dayforecast.getDouble(OWM_PRESSURE);
                humidity=dayforecast.getInt(OWM_HUMIDITY);
                windSpeed=dayforecast.getDouble(OWM_WINDSPEED);
                windDirection=dayforecast.getDouble(OWM_WIND_DIRECTION);

                JSONObject temp=dayforecast.getJSONObject(OWM_TEMPERATURE);
                high=temp.getDouble(OWM_MAX);
                low=temp.getDouble(OWM_MIN);

                JSONObject weather=dayforecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description=weather.getString(OWM_DESCRIPTION);
                weatherId=weather.getInt(OWM_WEATHER_ID);

                ContentValues weatherValues=new ContentValues();

                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, mLocation_id);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);

                cVVector.add(weatherValues);
            }
            /*
            for(int x=0;x<cVVector.size();x++){
                Log.i(LOG_TAG,cVVector.get(x).toString());
            }*/



            if(cVVector.size()>0){
                ContentValues[] cvArray=new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                Json_context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,cvArray);
            }

            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";  //date ASC

            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                    locationSetting, System.currentTimeMillis());
            //uri
            Log.i(LOG_TAG,weatherForLocationUri.toString());
            Cursor cr=Json_context.getContentResolver().query(weatherForLocationUri,null,null,null,sortOrder);


            cVVector=new Vector<ContentValues>(cr.getCount());

            if(cr.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cr, cv);
                    cVVector.add(cv);
                }
                while (cr.moveToNext());
            }
                String[] returned_results=convertContentValuesToUXFormat(cVVector);
                return  returned_results;

        }catch (JSONException e){
        Log.i(LOG_TAG,e.getMessage().toString());
        e.printStackTrace();
        }


        return null;




    }


    @Override
    protected String[] doInBackground(Object... mEntry){

        String json_text_entry=(String) mEntry[0];
        String Location_setting=(String) mEntry[1];


//        Log.i(LOG_TAG,json_text_entry+"\n"+Location_setting+"\n"+Integer.toString(Day_count_entry));


        try {
            return getWeatherDataFromJson(json_text_entry,Location_setting);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
