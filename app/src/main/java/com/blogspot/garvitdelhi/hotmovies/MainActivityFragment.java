package com.blogspot.garvitdelhi.hotmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Boolean sort = true;

    ImageAdapter m_imageAdapter;
    private ArrayList<SingleMovie> m_weekForecastList = new ArrayList<SingleMovie>();

    public MainActivityFragment() {
    }

    private boolean networkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(
                        getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating the View with fragment_main
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialising ImageAdapter with MainActivity Context
        m_imageAdapter = new ImageAdapter(getActivity(), m_weekForecastList);

        // Finding the GridView inside the view and setting ImageAdapter as it'sadapter
        GridView gridview = (GridView) view.findViewById(R.id.gridview_movie_posters);
        gridview.setAdapter(m_imageAdapter);

        // Setting onClickListener to display toast
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent detailActivityIntent = new Intent(getActivity(), MovieDetail.class)
                        .putExtra("SingleMovie", m_weekForecastList.get(position));

                startActivity(detailActivityIntent);
            }
        });

        new fetchMoviesPosters().execute(1);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_sort_popularity) {
            sort = true;
            new fetchMoviesPosters().execute(1);
            return true;
        } else if(item.getItemId() == R.id.action_sort_Rating) {
            sort = false;
            new fetchMoviesPosters().execute(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class fetchMoviesPosters extends AsyncTask<Integer, Void, String[]> {

        private final String LOG_TAG = fetchMoviesPosters.class.getSimpleName();
        private String RELEASE_DATE_GTE = "primary_release_date.gte";
        private String RELEASE_DATE_LTE = "primary_release_date.lte";
        private String PAGE_NUMBER = "pageno";
        private String API_KEY = "api_key";
        private String IMAGE_CONFIGURATION = "images";
        private String BASE_URL_CONFIGURATION = "base_url";
        private String IMAGE_QUALITY = "w185";
        private String IMAGE_QUALITY_LARGE = "w500";
        private String SORT_BY = "sort_by";
        private String LANGUAGE = "language";
        private String pref_language = "en";

        private String baseUrl = "https://api.themoviedb.org/3/discover/movie";
        private String baseUrlConfiguration = "https://api.themoviedb.org/3/configuration";
        private String baseImageURL;

        private String datetoString(Integer numDays) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, numDays);
            Date date = calendar.getTime();
            Log.e(LOG_TAG, dateFormat.format(date));
            return dateFormat.format(date);
        }

        private String[] getPosters(String movieInfoStr)
                throws JSONException{

            JSONObject movieInfoJson = new JSONObject(movieInfoStr);
            JSONArray movieInfoResults = movieInfoJson.getJSONArray("results");
            String[] movieResulsList = new String[movieInfoResults.length()];
            for(int i = 0; i < movieInfoResults.length(); ++i) {
                movieResulsList[i] = movieInfoResults.getString(i);
            }

            return movieResulsList;
        }

        @Override
        protected String[] doInBackground(Integer... params) {
            if (!networkConnected()) {
                return null;
            }

            Uri builtURiConfiguration = Uri.parse(baseUrlConfiguration).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY).build();

            try {
                URL urlConfiguration = new URL(builtURiConfiguration.toString());
                String configuration = sendGETRequest(urlConfiguration);
                JSONObject imageConfiguration = new JSONObject(configuration).getJSONObject(IMAGE_CONFIGURATION);
                baseImageURL = imageConfiguration.getString(BASE_URL_CONFIGURATION);

                // Trying to make a connection with HttpURLConnection
                Uri.Builder builtUri = Uri.parse(baseUrl).buildUpon();

                String pref_sort_order_value;

                if(sort) {
                    pref_sort_order_value = getString(R.string.pref_sort_popularity_value);
                } else {
                    pref_sort_order_value = getString(R.string.pref_sort_rating_value);
                }


                builtUri.appendQueryParameter(SORT_BY, pref_sort_order_value)
                        .appendQueryParameter(PAGE_NUMBER, Integer.toString(params[0]))
                        .appendQueryParameter(LANGUAGE, pref_language)
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY);

                URL url = new URL(builtUri.build().toString());
                String movieInfoStr = sendGETRequest(url);
                return getPosters(movieInfoStr);

            } catch (IOException|JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        private String sendGETRequest(URL url) {
            // Using HttpURLConnection to make GET request to api server
            HttpURLConnection urlConnection = null;

            // BufferedReader used to store the data response into buffer before converting it into string
            BufferedReader reader = null;

            // String to store the complete JSON response as String
            String JSONStr = null;

            try {

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

                JSONStr = buffer.toString();

                return JSONStr;

            } catch (IOException e) {
                // Catch HttpURLConnection exception and log it.
                Log.e(LOG_TAG, "Error ", e);
                return null;

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
        }

        private SingleMovie getSingleMovie(String jsonStr)
                throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String imageUrl = jsonObject.getString("poster_path");
            Uri imagePath = Uri.parse(baseImageURL).buildUpon()
                    .appendPath(IMAGE_QUALITY)
                    .appendEncodedPath(imageUrl)
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY).build();

            Uri imagePathLarge = Uri.parse(baseImageURL).buildUpon()
                    .appendPath(IMAGE_QUALITY_LARGE)
                    .appendEncodedPath(imageUrl)
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY).build();

            return new SingleMovie(
                    jsonObject.getString("original_title"),
                    imagePath.toString(),
                    imagePathLarge.toString(),
                    jsonObject.getString("overview"),
                    jsonObject.getString("release_date"),
                    jsonObject.getString("vote_average")
            );
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if(strings != null && strings.length > 0) {
                try {
                    m_weekForecastList.clear();
                    for (int i = 0; i < strings.length; ++i) {
                        SingleMovie movie = getSingleMovie(strings[i]);
                        m_weekForecastList.add(movie);
                    }
                    m_imageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error ", e);
                }

            }
        }
    }

}
