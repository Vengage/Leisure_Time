package com.mta.vengage.leisuretime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.mta.vengage.leisuretime.data.TablesContract;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        updateWeather();
        updateCinemaService();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Main.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getApplicationContext());
        String location = Utility.getPreferredLocation(getApplicationContext());

        getContentResolver().delete(TablesContract.WeatherEntry.CONTENT_URI,null,null);
        getContentResolver().delete(TablesContract.LocationEntry.CONTENT_URI,null,null);

        weatherTask.execute(location);
    }

    private void updateCinemaService() {
        FetchCinemaTask cinemaTask = new FetchCinemaTask(getApplicationContext());

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        Date dt = new Date(System.currentTimeMillis());
        String currentDate = "\"" + timeFormat.format(dt) + "\"";


        getContentResolver().delete(TablesContract.MoviesEntry.CONTENT_URI,null,null);
        getContentResolver().delete(TablesContract.ProgramEntry.CONTENT_URI,null,null);

        cinemaTask.execute(currentDate);
    }
}
