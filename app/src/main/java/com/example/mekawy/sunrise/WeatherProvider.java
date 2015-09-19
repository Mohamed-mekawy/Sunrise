package com.example.mekawy.sunrise;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {

    private WeatherDbHelper mOpenHelper;
    private final static UriMatcher sUriMatcher=buildUriMatcher();

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;


    static UriMatcher buildUriMatcher() {
        UriMatcher DB_Mathcer=new UriMatcher(UriMatcher.NO_MATCH);
        String authority=WeatherContract.CONTENT_AUTHORITY;
        DB_Mathcer.addURI(authority,WeatherContract.PATH_WEATHER,WEATHER);
        DB_Mathcer.addURI(authority,WeatherContract.PATH_WEATHER+"/*",WEATHER_WITH_LOCATION);
        DB_Mathcer.addURI(authority,WeatherContract.PATH_WEATHER+"/*/#",WEATHER_WITH_LOCATION_AND_DATE);
        DB_Mathcer.addURI(authority,WeatherContract.PATH_LOCATION,LOCATION);
        return DB_Mathcer;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper=new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
