package com.mta.vengage.leisuretime;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mta.vengage.leisuretime.data.TablesContract;


public class MovieActivity extends ActionBarActivity {


    private MoviesAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        Uri moviesUri = TablesContract.MoviesEntry.buildMoviesMoviesUri();
        Cursor cursor = getApplicationContext().getContentResolver().query(moviesUri, null,null,null,null);
        mMovieAdapter = new MoviesAdapter(getApplicationContext(),cursor, 0);
        ListView listView = (ListView) findViewById(R.id.listview_movie);
        listView.setAdapter(mMovieAdapter);


        // Dupa ce realizam activitatea moviedetail
        // mai trebuie adaugat si un query prin care
        // sa introducem date despre programul filmului
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//                if(cursor != null){
//                    String movie_id = parent.findViewById(R.id.invisible_movie_id);
//                    Intent intent = new Intent(getApplicationContext(), MovieDetail.class)
//                            .setData(TablesContract.MoviesEntry.buildMoviesMovieUri(movie_id));
//                    startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



}
