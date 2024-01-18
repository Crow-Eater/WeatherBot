package org.example;

import java.io.IOException;
import java.net.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;


public class WeatherParser
{
    private String firstPart = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private String secondPart = "&units=metric&appid=646a7bd7dd325efce8b8b36ea9f33496";
    public String getWeather(String city)
    {
        try
        {
            URL url = new URL(firstPart + city + secondPart);
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
                    if (forecastTime.contains("09:00") || forecastTime.contains("18:00") || forecastTime.contains("15:00")) {
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

    String toString(List<String> weatherList) throws com.fasterxml.jackson.core.JsonProcessingException, IOException
    {
        StringBuffer buf = new StringBuffer();
        for(String s: weatherList) {

            JsonNode dateNode = new ObjectMapper().readTree(s).get("dt_txt");
            String dateTime = dateNode.toString();

            LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String formattedDateTime = forecastDateTime.format(DateTimeFormatter.ofPattern("MMM-dd HH:mm", Locale.US));

            JsonNode mainNode = new ObjectMapper().readTree(s).get("main");
            String temp = mainNode.get("temp").toString();
            String feels_like = mainNode.get("feels_like").toString();

            JsonNode weatherNode = new ObjectMapper().readTree(s).get("weather");
            String weather = weatherNode.get(0).get("main").toString();

            JsonNode windNode = new ObjectMapper().readTree(s).get("wind");
            String wind = windNode.get("speed").toString();

            buf.append(formattedDateTime + "   Температура: " + temp + " Ощущается: " + feels_like + " " + weather + " Скорость ветра: " + wind + "\n");

        }
        return buf.toString();
    }



}
