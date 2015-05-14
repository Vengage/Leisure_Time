package com.mta.vengage.leisuretime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;

import com.mta.vengage.leisuretime.data.TablesContract;
import com.mta.vengage.leisuretime.data.TablesContract.WeatherEntry;

public class Main extends Activity {

    static final String DETAIL_URI = "URI";

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + TablesContract.WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            TablesContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMainPage();
    }

    private void setupMainPage() {

        Button movie = (Button) findViewById(R.id.movieButton);
        Button weather = (Button) findViewById(R.id.weatherButton);

        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CinemaActivity.class);
                startActivity(intent);
            }
        });

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Time dayTime = new Time();
                dayTime.setToNow();
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
                dayTime = new Time();
                long datetime = dayTime.setJulianDay(julianStartDay);

                Uri mUri = TablesContract.WeatherEntry.buildWeatherLocation(Utility.getPreferredLocation(getApplicationContext()));
                Cursor cursor = getContentResolver().query(mUri, DETAIL_COLUMNS, null, null, null);
                cursor.moveToNext();
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), WeatherDetailActivity.class)
                            .setData(TablesContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }

            }
        });


    }
}
