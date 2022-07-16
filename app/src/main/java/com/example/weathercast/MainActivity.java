package com.example.weathercast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    //deklaracja zmiennych
    final String API_ID = "dab3af44de7d24ae7ff86549334e45bd";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String Location_Provider = LocationManager.GPS_PROVIDER;
    TextView nameofCity;
    TextView weatherState;
    TextView temperature;

    TextView wind;
    TextView humidity;
    TextView pressure;

    ImageView mWeatherIcon;
    RelativeLayout mCityFinder;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String Location_Provider = LocationManager.GPS_PROVIDER;
        //Deklaracja id dla zmiennych
        nameofCity = findViewById(R.id.cityName);
        weatherState = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        mWeatherIcon = findViewById(R.id.weatherIcon);
        mCityFinder = findViewById(R.id.cityFinder);

        //Details Section
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);


        //Listner do buttona ktory przenosi nas do activity cityFinder
        mCityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cityFinder.class);
                startActivity(intent);
            }
        });
    }

    //Pobieramy pogode dla nowej lokacji za pomoca metody
   /* @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    } */

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        String city=mIntent.getStringExtra("City");

        if(city!=null)
        {
            getWeatherForNewLocation(city);
        }
        else
        {
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewLocation(String city)
    {
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",API_ID);
        executeApiQuery(params);;
    }

    //Deklarujemy metode użytą powyżej
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onLocationChanged(@NonNull Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon",Longitude);
                params.put("appid",API_ID);
                executeApiQuery(params);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Toast.makeText(MainActivity.this,"włącz GPS",Toast.LENGTH_SHORT).show();

            }


        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);



    }


    //Ustawiamy Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"Locationget Succesfully",Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else
            {
                //użytkownik odrzucil zapytanie
            }
        }
    }

    private void executeApiQuery(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                Toast.makeText(MainActivity.this,"Data Get Succes",Toast.LENGTH_SHORT).show();


                //
                weatherData weatherD = weatherData.fromJson(response);
                updateUI(weatherD);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);


            }

        });

    }


    //zmieniamy tekst pozycji na nowo pobrane
    private void updateUI(weatherData weather){

        temperature.setText(weather.getmTemperature());
        nameofCity.setText(weather.getmCity());
        weatherState.setText(weather.getmWeatherType());

        //Tworzymy temporary Stringa aby przesłać go do metody capitalize ktora zmieni pierwsza litere na dużą
        String temp;
        temp = weather.getmWeatherType();
        weatherState.setText(capitalize(temp));


        wind.setText(weather.getmWind());
        humidity.setText(weather.getmHumidity());
        pressure.setText(weather.getmPressure());




        //pobieramy informacje o sciezce do ikony pogody
        int resourceID=getResources().getIdentifier(weather.getmWeatherIcon(),"drawable",getPackageName());
        mWeatherIcon.setImageResource(resourceID);


    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager != null)
        {
            mLocationManager.removeUpdates(mLocationListner);

        }
    }








}
