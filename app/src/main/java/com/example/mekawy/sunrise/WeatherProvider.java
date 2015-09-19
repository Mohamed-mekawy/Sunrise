package com.example.mekawy.sunrise;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {

    private WeatherDbHelper mOpenHelper;
    private final static UriMatcher sUriMatcher=buildUriMatcher();

    private static SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;


    //Quiry builder for more structured
    static {
        sWeatherByLocationSettingQueryBuilder=new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME+ " INNER JOIN "+
                        WeatherContract.LocationEntry.TABLE_NAME+ " ON "+
                        WeatherContract.WeatherEntry.TABLE_NAME +"."+
                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = "+
                        WeatherContract.LocationEntry.TABLE_NAME+"."+
                        WeatherContract.LocationEntry._ID
        );
    }


    //location.location_setting = ?
    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    //location.location_setting = ? AND date >= ?    "StartDate"
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {

        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri); // return such 94044
        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getWeatherByLocationSettingAndDate(Uri uri, String[] projection, String sortOrder) {

        String location=WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long single_date = WeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),

                projection,
                sLocationSettingAndDaySelection,
                new String[]{location,Long.toString(single_date)},
                null,
                null,
                sortOrder
                );
    }



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
    public Cursor query(Uri uri, String[] projection, String selelction, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case WEATHER: {
                retCursor =mOpenHelper.getReadableDatabase().
                        query(WeatherContract.WeatherEntry.TABLE_NAME,
                                projection,
                                selelction,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().
                        query(WeatherContract.LocationEntry.TABLE_NAME,
                                projection,
                                selelction,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper=new WeatherDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        //out one of four URI's
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_DIR_TYPE;
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_DIR_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WEATHER: {
                normalizeDate(values);
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case LOCATION:{
                long _id=db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri= WeatherContract.LocationEntry.buildLocationUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
            values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue));
        }
    }


    @Override
    public int delete(Uri uri, String Selection, String[] SelectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int _del;

        //will delete all rows and return deleted rows numbers
        if(Selection==null)Selection="1";

        switch(match){
            case WEATHER:{
                _del=db.delete(WeatherContract.WeatherEntry.TABLE_NAME,Selection,SelectionArgs);
                break;
            }
            case LOCATION:{
                _del=db.delete(WeatherContract.LocationEntry.TABLE_NAME,Selection,SelectionArgs);
                break;
            }
        default:    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(_del != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return _del;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection , String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int _update;

        switch (match){
            case LOCATION:{
                _update=db.update(WeatherContract.LocationEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }

            case WEATHER:{
                normalizeDate(values);
                _update=db.update(WeatherContract.WeatherEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if(_update !=0){
                getContext().getContentResolver().notifyChange(uri,null);
            }

        return _update;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case WEATHER: {
                db.beginTransaction();
                int retCount=0;

                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long Bulk = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if(Bulk !=-1)retCount++;
                    }
                    db.setTransactionSuccessful();
                }

                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return retCount;
            }
            default:return super.bulkInsert(uri,values);
        }
    }






}
