package org.example.demo2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class weather {
    private static String weatherInfo = "";
    private static String icon = "";
    private static String mainWeather = "";
    private static String temp = "";
    private static String humidity = "";


    private static void getJson() {
        // OpenWeatherMap API的URL
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=Liverpool&appid=f0e3fc18a1b8b510513f1e13e12f8682";

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // get data
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // print
            System.out.println(content.toString());
            weatherInfo = content.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getWeatherInfo() {
        JSONObject jsonObject = JSON.parseObject(weatherInfo);

        // Extract weather information
        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        JSONObject weather = weatherArray.getJSONObject(0);
        icon = weather.getString("icon");
        mainWeather = weather.getString("main");

        // Extract main info
        JSONObject main = jsonObject.getJSONObject("main");
        temp = String.valueOf(main.getDoubleValue("temp"));
        humidity = String.valueOf(main.getIntValue("humidity"));

        // print
        System.out.println("Weather Icon: " + icon);
        System.out.println("Weather Main: " + mainWeather);
        System.out.println("Temperature: " + temp);
        System.out.println("Humidity: " + humidity);

    }
    public void refreshWeather() {
        getJson();
        getWeatherInfo();
    }
    public  String getIcon(){
        return icon;
    }
    public  String getMainWeather(){
        return mainWeather;
    }
    public  String getTemp(){
        return temp;
    }
    public  String getTempCentigrade(){
        double tempCentigrade = (Double.parseDouble(temp)-32)/1.8;
        return String.valueOf(tempCentigrade);
    }
    public  String getHumidity(){
        return humidity;
    }


}
