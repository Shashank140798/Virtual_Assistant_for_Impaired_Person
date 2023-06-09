package com.example.vision;

import android.app.ProgressDialog;

import android.net.Uri;
import android.os.AsyncTask;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.Date;

public class FetchWeatherTask extends AsyncTask<String, Void, WeatherData> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private boolean DEBUG = true;

    private String getReadableDateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }



    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private WeatherData getWeatherDataFromJson(String forecastJsonStr,
                                            String locationSetting)
            throws JSONException {


        final String OWM_CITY_NAME = "name";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        final String OWM_HUMIDITY = "humidity";


        try {
            WeatherData weatherData = new WeatherData();
            JSONObject responseJson = new JSONObject(forecastJsonStr);

            JSONObject forecastJson = responseJson.getJSONObject("current");
            weatherData.cityName = responseJson.getJSONObject("location").getString(OWM_CITY_NAME);

            weatherData.lat = responseJson.getJSONObject("location").getDouble(OWM_LATITUDE);
            weatherData.lon = responseJson.getJSONObject("location").getDouble(OWM_LONGITUDE);


                weatherData.pressure = forecastJson.getDouble("pressure_mb");
                weatherData.humidity = forecastJson.getInt(OWM_HUMIDITY);
                weatherData.windSpeed = forecastJson.getDouble("wind_kph");
                weatherData.windDirection = forecastJson.getString("wind_dir");
                weatherData.temp = forecastJson.getDouble("temp_c");

            Log.d(LOG_TAG, "FetchWeatherTask Complete");


            return weatherData;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected WeatherData doInBackground(String... params) {


        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;


        try {

            final String FORECAST_BASE_URL =
                    "https://api.weatherapi.com/v1/current.json?";
            final String QUERY_PARAM = "q";
            final String AQI = "aqi";
            final String KEY = "key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY, "12bdd11f52464ee6bb673638222512")
                    .appendQueryParameter(QUERY_PARAM, locationQuery)
                    .appendQueryParameter(AQI, "no")

                    .build();


            URL url = new URL(builtUri.toString());


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
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
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
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

        try {
            return getWeatherDataFromJson(forecastJsonStr, locationQuery);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

}