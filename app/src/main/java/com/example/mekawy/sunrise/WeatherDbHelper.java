package com.example.mekawy.sunrise;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDbHelper extends SQLiteOpenHelper{

    private static final int DB_Version=1;
    private static final String DB_name="weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DB_name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(WeatherDbHelper.class.getSimpleName(),"onCreate DB helper");
        //location table
        //Table Like : _ID | location_setting | city_name | coord_lat | coord_long
        final String SQL_CREATE_LOCATION_TABLE="CREATE TABLE "+WeatherContract.LocationEntry.TABLE_NAME +
                " ( "+ WeatherContract.LocationEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING+" TEXT UNIQUE NOT NULL, "+
                WeatherContract.LocationEntry.COLUMN_CITY_NAME+" TEXT NOT NULL , "+
                WeatherContract.LocationEntry.COLUMN_COORD_LAT+"REAL NOT NULL , "+
                WeatherContract.LocationEntry.COLUMN_COORD_LONG+"REAL NOT NULL );";

        // Table: _ID | location_id (fk)| date | weather_id "icon" | short_desc | min | max |humadity | pressure |wind | degree "direction"
        final String SQL_CREATE_WEATHER_TABLE="CREATE TABLE "+WeatherContract.WeatherEntry.TABLE_NAME+ " (" +
        WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this weather data
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +
                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" +WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

                    sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
                    sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    // onUpgrade method doesn't depend on Applicaation version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+WeatherContract.WeatherEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+WeatherContract.LocationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
