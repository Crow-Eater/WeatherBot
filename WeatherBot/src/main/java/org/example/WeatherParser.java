package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WeatherParser
{
    private String firstForecastPart = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private String firstCurrentPart = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String secondPart = "&units=metric&appid=646a7bd7dd325efce8b8b36ea9f33496";
    public String getForecastWeather(String city)
    {
        try
        {
            URL url = new URL(firstForecastPart + city + secondPart);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String data = response.toString();

            JsonNode arrNode = new ObjectMapper().readTree(data).get("list");

            List<String> weatherList = new ArrayList<>();

            if (arrNode.isArray()) {
                for (final JsonNode objNode : arrNode) {
                    String forecastTime = objNode.get("dt_txt").toString();
                    if (forecastTime.contains("09:00") || forecastTime.contains("21:00") || forecastTime.contains("15:00")) {
                        weatherList.add(objNode.toString());
                    }
                }
            }

            String result = String.format("%s:%s%s", city, System.lineSeparator(), toString(weatherList));

            return result;
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
    }

    public String getCurrentWeather(String city)
    {
        try
        {
            URL url = new URL(firstCurrentPart + city + secondPart);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String data = response.toString();

            System.out.println(data);

            JsonNode dateNode = new ObjectMapper().readTree(data).get("dt");
            long dateTime = Long.parseLong(dateNode.toString());
            java.util.Date time = new java.util.Date(dateTime*1000);

            return time + getWeather(data);
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
    }

    String toString(List<String> weatherList) throws com.fasterxml.jackson.core.JsonProcessingException, IOException
    {
        StringBuffer buf = new StringBuffer();
        for(String s: weatherList) {

            JsonNode dateNode = new ObjectMapper().readTree(s).get("dt_txt");
            String dateTime = dateNode.toString();

            LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedDateTime = forecastDateTime.format(DateTimeFormatter.ofPattern("MMM-dd HH:mm", Locale.US));


            buf.append(formattedDateTime + getWeather(s) + "\n\n");

        }
        return buf.toString();
    }

    private String getWeather(String data) throws IOException {
        JsonNode mainNode = new ObjectMapper().readTree(data).get("main");
        String temp = mainNode.get("temp").toString();
        String feels_like = mainNode.get("feels_like").toString();

        JsonNode weatherNode = new ObjectMapper().readTree(data).get("weather");
        String weather = weatherNode.get(0).get("main").toString();

        weather = weather.substring(1, weather.length() - 1);
        switch (weather)
        {
            case "Clouds":
            {
                weather = "☁";
                break;
            }
            case "Clear":
            {
                weather = "☀";
                break;
            }
            case "Rain":
            {
                weather = "☔";
                break;
            }
            case "Snow":
            {
                weather = "❄";
                break;
            }
        }

        JsonNode windNode = new ObjectMapper().readTree(data).get("wind");
        String wind = windNode.get("speed").toString();

        return " " + weather + "\nТемпература: " + Math.round(Double.parseDouble(temp)) + "\nОщущается: " + Math.round(Double.parseDouble(feels_like))  + "\nСкорость ветра: " + Math.round(Double.parseDouble(wind));
    }

}
