package com.mta.vengage.leisuretime;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Vasile Cosovanu on 5/10/2015.
 * <p/>
 * CursorAdapter for MovieActivity
 */
public class MoviesAdapter extends CursorAdapter {


    static final int COL_MOVIE_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DURATION = 2;
    static final int COL_GENRE = 3;
    static final int COL_MIN_AGE = 4;
    static final int COL_TYPE = 5;
    static final int COL_POSTER = 6;
    static final int COL_SYNOPSIS = 7;


    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movies, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        TextView name = (TextView) view.findViewById(R.id.title_movie);
        TextView duration = (TextView) view.findViewById(R.id.duration);
        TextView genre = (TextView) view.findViewById(R.id.genre);
        TextView type = (TextView) view.findViewById(R.id.type);
        TextView movie_id = (TextView) view.findViewById(R.id.movie_id);

        name.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        genre.setText(cursor.getString(cursor.getColumnIndexOrThrow("genre")));
         type.setText(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        duration.setText(cursor.getString(cursor.getColumnIndexOrThrow("duration")) + " min");
        movie_id.setText(cursor.getString(cursor.getColumnIndexOrThrow("movie_id")));
        Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndexOrThrow("poster")))
                .resize(80, 80)
                .into(thumbnail);

    }
}
