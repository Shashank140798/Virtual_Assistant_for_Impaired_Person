package com.example.vision;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LocationWeatherActivity extends AppCompatActivity {

    TextView temp;
    TextView location;
    TextView humidity;
    TextView pressure;
    TextView windSpeed;
    TextView windDir;
    EditText editText;

    TextToSpeech textToSpeech;
    private final String LOG_TAG = LocationWeatherActivity.class.getSimpleName();

    Button displayData;
    WeatherData weatherData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationweather);
        temp = (TextView) findViewById(R.id.temp);
        location = (TextView) findViewById(R.id.Location);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windDir = (TextView) findViewById(R.id.windDir);

        displayData = (Button) findViewById(R.id.displayData);

        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
                editText = (EditText) findViewById(R.id.textInput);
                String loc = String.valueOf(editText.getText());
                if(loc==null || loc.equals(""))
                {
                    loc="bangalore";
                }
                try {
                    weatherData = fetchWeatherTask.execute(loc).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                windDir.setText(weatherData.windDirection);
                location.setText(weatherData.cityName);
                pressure.setText(weatherData.pressure+" mb");
                humidity.setText(weatherData.humidity+"");
                windSpeed.setText(weatherData.windSpeed+" kph");
                temp.setText(weatherData.temp+"C");

                String wst = "The weather for "+ loc + "is " + "Temperature "+ weatherData.temp +" degree celsius"
                        + " humidity" + weatherData.humidity + "wind Speed " + weatherData.windSpeed + "kilometer per hour" +
                        "pressure " + weatherData.pressure + "millibars";
                textToSpeech.speak(wst, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });


    }
}
