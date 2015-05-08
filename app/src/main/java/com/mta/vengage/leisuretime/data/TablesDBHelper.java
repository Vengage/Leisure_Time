package com.mta.vengage.leisuretime.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mta.vengage.leisuretime.data.TablesContract.LocationEntry;
import com.mta.vengage.leisuretime.data.TablesContract.MoviesEntry;
import com.mta.vengage.leisuretime.data.TablesContract.ProgramEntry;
import com.mta.vengage.leisuretime.data.TablesContract.WeatherEntry;

/**
 * Created by Vasile Cosovanu on 5/1/2015.
 * <p/>
 * <p/>
 * This is the SQLiteOpenHelper class which creates the database
 */
public class TablesDBHelper extends SQLiteOpenHelper {

    // La orice modificare versiunea bazei de date trebuie incrementata manual
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "leisuretime.db";


    private Context mContext;

    public TablesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                ");";


        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY, " +

                WeatherEntry.COLUMN_LOCATION_KEY + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +

                WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL," +

                " FOREIGN KEY (" + WeatherEntry.COLUMN_LOCATION_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +
                " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                WeatherEntry.COLUMN_LOCATION_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY, " +

                MoviesEntry.COlUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_DURATION + " INTEGER NOT NULL, " + // in minutes
                MoviesEntry.COLUMN_GENRE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MIN_AGE + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                MoviesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL" +
                ");";

        final String SQL_CREATE_PROGRAM_TABLE = "CREATE TABLE " + ProgramEntry.TABLE_NAME + " (" +
                ProgramEntry._ID + " INTEGER PRIMARY KEY, " +

                ProgramEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ProgramEntry.COLUMN_HOUR + " TEXT NOT NULL, " +

                " FOREIGN KEY (" +  ProgramEntry.COLUMN_MOVIE_ID  + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry.COlUMN_MOVIE_ID + ")" +
                ");";



        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_PROGRAM_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProgramEntry.TABLE_NAME);
        onCreate(db);
    }

}
