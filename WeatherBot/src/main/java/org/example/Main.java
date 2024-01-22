package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.MalformedURLException;

public class Main {
    public static void main(String[] args) throws MalformedURLException, IOException, Exception
    {
        WeatherParser wp = new WeatherParser();


        System.out.println(wp.getForecastWeather("Minsk"));
        System.out.println(wp.getCurrentWeather("Minsk"));


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(new WeatherBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        catch (com.google.common.util.concurrent.ExecutionError e)
        {
            e.printStackTrace();
        }





    }
}