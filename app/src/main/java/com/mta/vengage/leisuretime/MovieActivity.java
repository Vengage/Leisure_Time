package com.mta.vengage.leisuretime;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mta.vengage.leisuretime.data.TablesContract;


public class MovieActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int MOVIES_LOADER = 0;

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

    static final int COL_MOVIE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DURATION = 2;
    static final int COL_GENRE = 3;
    static final int COL_MIN_AGE = 4;
    static final int COL_TYPE = 5;
    static final int COL_POSTER = 6;
    static final int COL_SYNOPSIS = 7;


    private MoviesAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        Uri moviesUri = TablesContract.MoviesEntry.buildMoviesMoviesUri();
        Cursor cursor = getApplicationContext().getContentResolver().query(moviesUri, null, null, null, null);
        mMovieAdapter = new MoviesAdapter(getApplicationContext(), cursor, 0);
        ListView listView = (ListView) findViewById(R.id.listview_movie);
        listView.setAdapter(mMovieAdapter);


        // Dupa ce realizam activitatea moviedetail
        // mai trebuie adaugat si un query prin care
        // sa introducem date despre programul filmului
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    TextView tv_movieID = (TextView) view.findViewById(R.id.movie_id);
                    String movie_id = tv_movieID.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                    intent.putExtra("movie_id", movie_id);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = TablesContract.MoviesEntry.COLUMN_MOVIE_ID + " ASC";
        Uri moviesUri = TablesContract.MoviesEntry.buildMoviesMoviesUri();

        return new CursorLoader(getApplicationContext(),
                moviesUri,
                MOVIES_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
