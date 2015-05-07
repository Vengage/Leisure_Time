package com.mta.vengage.leisuretime.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Vasile Cosovanu on 5/1/2015.
 *
 * Contract in care sunt declarate numele de coloane pentru tabele din baza de date
 */
public class WeatherContract {


    public static final String CONTENT_AUTHORITY = "com.mta.vengage.leisuretime";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_CINEMA = "cinema";

    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;


        // Numele tabelului
        public static final String TABLE_NAME = "location";

        // cheie straina
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // numele orasului
        public static final String COLUMN_CITY_NAME = "city_name";

        // coordonate latitudine
        public static final String COLUMN_COORD_LAT = "coord_lat";

        // coordonate longitudine
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        // numele tabelului
        public static final String TABLE_NAME = "weather";

        // coloana cu cheia straina pentru legatura cu tabelul location
        public static final String COLUMN_LOCATION_KEY = "location_id";

        // coloana cu data
        public static final String COLUMN_DATE = "date";

        // id-ul intrarii in tabel
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // descrierea despre vreme
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // temperatura minima si maxima
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // umiditatea
        public static final String COLUMN_HUMIDITY = "humidity";

        // presiunea atmosferica
        public static final String COLUMN_PRESSURE = "pressure";

        // viteza vantului km/h
        public static final String COLUMN_WIND_SPEED = "wind";

        // grade meteorologice ( de la 0 la 180)
        public static final String COLUMN_DEGREES = "degrees";


        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }

    public static final class CinemaEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CINEMA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CINEMA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CINEMA;

        // Numele tabelului
        public static final String TABLE_NAME = "program_cinema";

        // Durata filmului
        public static final String COLUMN_DURATION = "duration";

        // Genul filmului
        public static final String COLUMN_GENRE = "genre";

        // Numele filmului
        public static final String COLUMN_NAME = "name";

        // Poster film
        public static final String COLUMN_POSTER = "poster";

        // Tipul filmului
        public static final String COLUMN_TYPE = "type";

        // Varsta minima
        public static final String COLUMN_MIN_AGE = "min_age";

        // Synopsis film
        public static final String COLUMN_SYNOPSIS = "synopsis";

        // Ora si data difuzarii
        public static final String COLUMN_HOUR = "hour";

        // Numarul de locuri din sala
        public static final String COLUMN_NR_SEATS = "nr_seats";

        // Sala ocupata
        public static final String COLUMN_OCUPIED = "ocupied";

        // Numarul salii
        public static final String COLUMN_SALA_ID = "sala_id";

        public static Uri buildCinemaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
