package com.example.mekawy.sunrise;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

//DB tables
//location table
//Table Like : _ID | location_setting | city_name | coord_lat | coord_long
//weather table
// _ID | location_id (fk)| date | weather_id "icon" | short_desc | min | max |humadity | pressure |wind | degree "direction"

public class WeatherContract {

    public static final String CONTENT_AUTHORITY ="com.example.mekawy.sunrise";
    final static Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);

    public final static String PATH_LOCATION="location";
    public final static String PATH_WEATHER="weather";

    //Conver from UNIX STAMP to julian date
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
    //_ID column will be added to table as the class implememnt BaseColumn
    public static final class LocationEntry implements BaseColumns {
        //ContentProvider URI STUFF
        public final static Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public final static String CONTENT_DIR_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE    +  "/" +    CONTENT_AUTHORITY   + "/" +    PATH_LOCATION;
        public final static String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE   +  "/" +    CONTENT_AUTHORITY   + "/" +    PATH_LOCATION;

        public static final String TABLE_NAME = "location";
        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        // Human readable location string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_CITY_NAME = "city_name";
        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
        //URI Function
        //content://com.example ~/location/No.
        public static Uri buildLocationUri(long loc){
            return ContentUris.withAppendedId(CONTENT_URI,loc);
        }

    }

    /* Inner class that defines the contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        //ContentProvider STUFF
        public final static Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public final static String CONTENT_DIR_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE    +"/"+   CONTENT_AUTHORITY   +"/"+   PATH_WEATHER;
        public final static String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE    +"/"+   CONTENT_AUTHORITY   +"/"+   PATH_WEATHER;

        public static final String TABLE_NAME = "weather";
        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";
        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";
        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";
        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";
        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";
        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        //URI Functions
        //content://com.example ~/weather/No.
        public static Uri buildWeatherUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        //content://com.example ~/weather/location
        public static Uri buildWeatherLocation(String locationSetting){// DIR
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }
        //content://com.example ~/weather/location?DATE=66846"normalizied Date"
        public static Uri buildWeatherLocationWithStartDate(String locationSetting,long start_Date){ //DIR
            long normalized_date=normalizeDate(start_Date);
            return CONTENT_URI.buildUpon().
                    appendPath(locationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(normalized_date)).build();
        }
        //content://com.example ~/weather/location/date"single"
        public static Uri buildWeatherLocationWithDate(String locationSetting,long single_Date){ //ITEM
            return CONTENT_URI.buildUpon().appendPath(locationSetting).
                    appendPath(Long.toString(normalizeDate(single_Date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri){
            String Q_parameter=uri.getQueryParameter(COLUMN_DATE);
            if(Q_parameter!=null && Q_parameter.length()>0)
                return Long.parseLong(Q_parameter);
            else return 0;
        }

    }


}
