package com.mta.vengage.leisuretime;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mta.vengage.leisuretime.data.TablesContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Vasile Cosovanu on 5/6/2015.
 * <p/>
 * Clasa care returneaza date de pe CinemaService
 */
public class FetchCinemaTask extends AsyncTask<String, Void, Void> {


    private final String LOG_TAG = FetchCinemaTask.class.getSimpleName();

    private final Context mContext;

    public FetchCinemaTask(Context context) {
        this.mContext = context;
    }

    private void getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String CS_FILM = "film";

        final String CS_FILM_ID = "film_id";
        final String CS_NAME = "nume";
        final String CS_DURATION = "durata";
        final String CS_GENRE = "genul";
        final String CS_TYPE = "tip";
        final String CS_MIN_AGE = "varsta_minima";
        final String CS_POSTER = "poster";
        final String CS_SYNOPSIS = "synopsis";

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesJson.length());

            for (int i = 1; i <= moviesJson.length(); i++) {

                JSONObject filmJson = moviesJson.getJSONObject(CS_FILM + i);

                int film_id;
                String name;
                int duration;
                String genre;
                String type;
                int min_age;
                String poster;
                String synopsis;

                film_id = filmJson.getInt(CS_FILM_ID);
                name = filmJson.getString(CS_NAME);
                duration = filmJson.getInt(CS_DURATION);
                genre = filmJson.getString(CS_GENRE);
                type = filmJson.getString(CS_TYPE);
                min_age = filmJson.getInt(CS_MIN_AGE);
                poster = filmJson.getString(CS_POSTER);
                synopsis = filmJson.getString(CS_SYNOPSIS);

                ContentValues movieValues = new ContentValues();
                movieValues.put(TablesContract.MoviesEntry.COLUMN_MOVIE_ID, film_id);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_NAME, name);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_DURATION, duration);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_MIN_AGE, min_age);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_POSTER, poster);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_GENRE, genre);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_TYPE, type);
                movieValues.put(TablesContract.MoviesEntry.COLUMN_SYNOPSIS, synopsis);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(TablesContract.MoviesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");

        } catch (
                JSONException e
                )

        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void getProgramDataFromJson(String programJsonStr)
            throws JSONException {

        final String CS_FILM = "film";

        final String CS_FILM_ID = "film_id";
        final String CS_HOUR = "ora";

        try {

            JSONObject programJson = new JSONObject(programJsonStr);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(programJson.length());

            for (int i = 1; i <= programJson.length(); i++) {

                JSONObject filmJson = programJson.getJSONObject(CS_FILM + i);

                int film_id;
                String ora;

                film_id = filmJson.getInt(CS_FILM_ID);
                ora = filmJson.getString(CS_HOUR);

                ContentValues programValues = new ContentValues();
                programValues.put(TablesContract.ProgramEntry.COLUMN_MOVIE_ID, film_id);
                programValues.put(TablesContract.ProgramEntry.COLUMN_HOUR, ora);

                cVVector.add(programValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(TablesContract.ProgramEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchProgramTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        String beginDate = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr;
        String programJsonStr;

        final String CINEMA_SERVICE_BASE_URL =
                "http://10.0.3.2/CinemaService/?";
        final String QUERY_MOVIES_PARAM = "movies";
        final String QUERY_HOURS_PARAM = "date";

        Uri moviesUri = Uri.parse(CINEMA_SERVICE_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_MOVIES_PARAM, "1")
                .build();
        Uri programUri = Uri.parse(CINEMA_SERVICE_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_HOURS_PARAM, beginDate)
                .build();

        try {
            URL url = new URL(moviesUri.toString());

            Log.d("Movies URL", url.toString());


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();
            getMoviesDataFromJson(moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        try {
            URL url = new URL(programUri.toString());

            Log.d("Program URI",url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            programJsonStr = buffer.toString();
            getProgramDataFromJson(programJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
