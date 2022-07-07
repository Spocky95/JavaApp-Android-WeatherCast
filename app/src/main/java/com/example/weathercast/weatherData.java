package com.example.weathercast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class weatherData {

    private String mTemperature,mWeatherIcon,mCity,mWeatherType;

    private int mContition;


    public static weatherData fromJson(JSONObject jsonObject)
    {
        //Proba wylapania parametrow kluczowych dla obiektu json
        try
        {
            weatherData weatherD = new weatherData();
            weatherD.mCity = jsonObject.getString("name");
            weatherD.mContition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

            weatherD.mWeatherIcon = updateWeatherIcon(weatherD.mContition);

            //zaokraglamy wynik w °F na °C
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue = (int)Math.rint(tempResult);

            //parsujemy do stringa
            weatherD.mTemperature = Integer.toString(roundedValue);

            return weatherD;
        }

        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private static String updateWeatherIcon(int condition)
    {

        if(condition>=0 && condition<=300)
        {
            return "cloudy";
        }
        else if(condition>=300 && condition<=500)
        {
            return "cloudy";
        }
        else if(condition>=500 && condition<=600)
        {
            return "cloudy";
        }
        else if(condition>=600 && condition<=700)
        {
            return "cloudy";
        }
        else if(condition>=700 && condition<=800)
        {
            return "cloudy";
        }
        else if(condition>=800 && condition<=1000)
        {
            return "cloudy";
        }

        return "anything";
    };

    public String getmTemperature() {
        return mTemperature + "°C";
    }

    public String getmWeatherIcon() {
        return mWeatherIcon;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
