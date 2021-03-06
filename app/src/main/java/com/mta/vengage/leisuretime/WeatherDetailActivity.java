package com.mta.vengage.leisuretime;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mta.vengage.leisuretime.data.TablesContract;
import com.mta.vengage.leisuretime.data.TablesContract.WeatherEntry;



public class WeatherDetailActivity extends ActionBarActivity {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private String mUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setElevation(0f);

        mLocation = Utility.getPreferredLocation(getApplicationContext());
        mUnits = Utility.getUnits(getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        if(location != null && !location.equals(mLocation)){
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null != df){
                df.onLocationChanged();
            }
            mLocation = location;
        }
        String units = Utility.getUnits(this);
        if(units != null && !units.equals(mUnits)){
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(null != df){
                df.onLocationChanged();
            }
            mUnits = units;
        }
    }

    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        static final String DETAIL_URI = "URI";

        private static final String FORECAST_SHARE_HASHTAG = " #LeisureTime";

        private ShareActionProvider mShareActionProvider;
        private String mForecast;
        private Uri mUri;

        private static final int DETAIL_LOADER = 0;

        private static final String[] DETAIL_COLUMNS = {
                WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
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


        private ImageView mIconView;
        private TextView mFriendlyDateView;
        private TextView mDateView;
        private TextView mDescriptionView;
        private TextView mHighTempView;
        private TextView mLowTempView;
        private TextView mHumidityView;
        private TextView mWindView;
        private TextView mPressureView;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            }


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
            mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
            mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
            mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
            mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
            mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
            mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
            mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (mForecast != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        void onLocationChanged() {
            updateWeather();
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

        void updateWeather() {
            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
            String location = Utility.getPreferredLocation(getActivity());

            getActivity().getContentResolver().delete(TablesContract.WeatherEntry.CONTENT_URI, null, null);
            getActivity().getContentResolver().delete(TablesContract.LocationEntry.CONTENT_URI, null, null);

            weatherTask.execute(location);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            String locationSetting = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = TablesContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());



            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToFirst()) {
                int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);

                mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

                long date = data.getLong(COL_WEATHER_DATE);
                String friendlyDateText = Utility.getDayName(getActivity(), date);
                String dateText = Utility.getFormattedMonthDay(getActivity(), date);
                mFriendlyDateView.setText(friendlyDateText);
                mDateView.setText(dateText);

                String description = data.getString(COL_WEATHER_DESC);
                mDescriptionView.setText(description);

                mIconView.setContentDescription(description);

                boolean isMetric = Utility.isMetric(getActivity());

                double high = data.getDouble(COL_WEATHER_MAX_TEMP);
                String highString = Utility.formatTemperature(getActivity(), high);
                mHighTempView.setText(highString);

                double low = data.getDouble(COL_WEATHER_MIN_TEMP);
                String lowString = Utility.formatTemperature(getActivity(), low);
                mLowTempView.setText(lowString);

                float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
                mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

                float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
                float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
                mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

                float pressure = data.getFloat(COL_WEATHER_PRESSURE);
                mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

                mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }
}