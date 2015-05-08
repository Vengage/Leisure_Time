package com.mta.vengage.leisuretime.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Vasile Cosovanu on 5/8/2015.
 *
 * This is the CinemaProvider
 */
public class CinemaProvider extends ContentProvider{

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TablesDBHelper mOpenHelper;


    static final int MOVIES = 100; // For insert, update, delete
    static final int MOVIES_MOVIES = 101; // To get movies
    static final int PROGRAM = 300; // For insert, update, delete
    static final int PROGRAM_MOVIE_PROGRAM = 301; // To get movie details


    private static final SQLiteQueryBuilder sMovieProgramQueryBuilder;

    // nu o sa folosim acest join intrucat combina prea multe informatii
    // care ar putea aduce intarzieri
    // in schimb vom folosi tabele separate din care vom lua informatiile
    // intrucat datele din acestea nu trebuiesc combinate
    // avand in vedere ca fac parte din activitati diferite
    static {
        sMovieProgramQueryBuilder = new SQLiteQueryBuilder();

        // This is an inner join which looks like
        // movie inner join program on movie.movie_id = program.movie.id
        sMovieProgramQueryBuilder.setTables(
                TablesContract.MoviesEntry.TABLE_NAME + " INNER JOIN " +
                        TablesContract.ProgramEntry.TABLE_NAME + " ON "
                + TablesContract.MoviesEntry.TABLE_NAME + "."
                + TablesContract.MoviesEntry.COlUMN_MOVIE_ID + "="
                + TablesContract.ProgramEntry.TABLE_NAME + "."
                        + TablesContract.ProgramEntry.COLUMN_MOVIE_ID
        );

    }

    // string-urile penutru interogari

    // program.movie_id = ?
    private static final String sProgramForMovieSelection =
            TablesContract.ProgramEntry.TABLE_NAME + "."
            + TablesContract.ProgramEntry.COLUMN_MOVIE_ID + " = ?";

    private  Cursor getMovies(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
            return new SQLiteQueryBuilder().query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
    }

    private Cursor getMovieProgram(Uri uri, String[] projection){

        Integer movid_id = TablesContract.ProgramEntry.getMovidIDFromUri(uri);
        String selection;
        String[] selectionArgs;

        if(movid_id == 0){
            Log.d("CinemaProvider","You fucked up! About movie_id not taking 0");
            selection = null;
            selectionArgs = null;
        }
        else {
            selection = sProgramForMovieSelection;
            selectionArgs =  new  String[]{movid_id.toString()};
        }

        return new SQLiteQueryBuilder().query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new TablesDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIES_MOVIES:
            {
                retCursor = getMovies(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            case PROGRAM_MOVIE_PROGRAM:
            {
                retCursor = getMovieProgram(uri, projection);
                break;
            }
            case MOVIES: // asta poate fi stearsa daca este duplicat
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TablesContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PROGRAM:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TablesContract.ProgramEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES_MOVIES:
                return TablesContract.MoviesEntry.CONTENT_TYPE;
            case PROGRAM_MOVIE_PROGRAM:
                return TablesContract.ProgramEntry.CONTENT_TYPE;
            case MOVIES:
                return TablesContract.MoviesEntry.CONTENT_TYPE;
            case PROGRAM:
                return TablesContract.ProgramEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
            {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for(ContentValues value: values){
                        long _id = db.insert(
                                TablesContract.MoviesEntry.TABLE_NAME,
                                null,
                                value
                        );
                        if( _id != -1 ){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case PROGRAM:
            {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for(ContentValues value: values){
                        long _id = db.insert(
                                TablesContract.ProgramEntry.TABLE_NAME,
                                null,
                                value
                        );
                        if( _id != -1 ){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
            {
                return super.bulkInsert(uri, values);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES: {
                long _id = db.insert(
                        TablesContract.MoviesEntry.TABLE_NAME,
                        null,
                        values
                );
                if( _id > 0 )
                    returnUri = TablesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " +  uri);
                break;
            }
            case PROGRAM: {
                long _id = db.insert(
                        TablesContract.ProgramEntry.TABLE_NAME,
                        null,
                        values
                );
                if( _id > 0 ){
                    returnUri = TablesContract.ProgramEntry.buildProgramUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match){
            case MOVIES:
            {
                rowsDeleted = db.delete(
                        TablesContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case PROGRAM:
            {
                rowsDeleted = db.delete(
                        TablesContract.ProgramEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        switch (match){
            case MOVIES:
            {
                rowsUpdated = db.update(
                        TablesContract.MoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case PROGRAM:
            {
                rowsUpdated = db.update(
                        TablesContract.ProgramEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher((UriMatcher.NO_MATCH));
        final String authority = TablesContract.CONTENT_AUTHORITY;

        // For each type of URI that we created , we create a corresponding code.
        matcher.addURI(authority, TablesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, TablesContract.PATH_MOVIES + "/1", MOVIES_MOVIES);

        matcher.addURI(authority, TablesContract.PATH_PROGRAM, PROGRAM);
        matcher.addURI(authority, TablesContract.PATH_PROGRAM + "/#", PROGRAM_MOVIE_PROGRAM);

        return matcher;
    }
}
