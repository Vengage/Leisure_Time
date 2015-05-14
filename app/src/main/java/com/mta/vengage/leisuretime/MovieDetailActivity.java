package com.mta.vengage.leisuretime;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mta.vengage.leisuretime.data.TablesContract;
import com.squareup.picasso.Picasso;


public class MovieDetailActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private static final String[] PROGRAM_COLUMNS = {
            TablesContract.ProgramEntry.COLUMN_MOVIE_ID,
            TablesContract.ProgramEntry.COLUMN_HOUR
    };

    private static final String[] MOVIES_COLUMNS = {
            TablesContract.MoviesEntry.COLUMN_MOVIE_ID,
            TablesContract.MoviesEntry.COLUMN_NAME,
            TablesContract.MoviesEntry.COLUMN_DURATION,
            TablesContract.MoviesEntry.COLUMN_GENRE,
            TablesContract.MoviesEntry.COLUMN_MIN_AGE,
            TablesContract.MoviesEntry.COLUMN_TYPE,
            TablesContract.MoviesEntry.COLUMN_POSTER,
            TablesContract.MoviesEntry.COLUMN_SYNOPSIS
    };

    static final int COL_NAME = 1;
    static final int COL_DURATION = 2;
    static final int COL_GENRE = 3;
    static final int COL_MIN_AGE = 4;
    static final int COL_TYPE = 5;
    static final int COL_POSTER = 6;
    static final int COL_SYNOPSIS = 7;

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_HOUR = 1;

    private TextView title;
    private TextView duration;
    private TextView genre;
    private TextView type;
    private TextView synopsis;
    private TextView  min_age;
    private ImageView poster;

    private TextView programView;


    private String movie_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        title = (TextView) findViewById(R.id.title_movie);
        duration = (TextView) findViewById(R.id.duration);
        genre = (TextView) findViewById(R.id.genre);
        type = (TextView) findViewById(R.id.type);
        synopsis = (TextView) findViewById(R.id.synopsis);
        min_age = (TextView) findViewById(R.id.min_age);
        poster = (ImageView) findViewById(R.id.poster);


        programView = (TextView) findViewById(R.id.program);
        programView.setText("");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getIntent();
        movie_id = intent.getExtras().getString("movie_id");
        if(intent == null)
            return null;

        CursorLoader cursorLoader = new CursorLoader(
                getApplicationContext(),
                TablesContract.MoviesEntry.buildMoviesMovieUri(movie_id),
                MOVIES_COLUMNS,
                TablesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movie_id},
                null
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!data.moveToFirst()){return;}

        title.setText(data.getString(COL_NAME));
        duration.setText(data.getString(COL_DURATION) + " min");
        genre.setText(data.getString(COL_GENRE));
        type.setText(data.getString(COL_TYPE));
        min_age.setText(data.getString(COL_MIN_AGE) + " ani");
        synopsis.setText(data.getString(COL_SYNOPSIS));

        Picasso
                .with(getApplicationContext())
                .load(data.getString(data.getColumnIndexOrThrow("poster")))
                .resize(175,271)
                .into(poster);

        Cursor cursor = getContentResolver().query(
                TablesContract.ProgramEntry.buildMovieProgramUri(movie_id),
                PROGRAM_COLUMNS,
                "movie_id=?",
                new String[]{movie_id},
                null);
        while(cursor.moveToNext()){
            programView.append(cursor.getString(COL_HOUR) + " ");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
