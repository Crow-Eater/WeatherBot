package org.example;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.telegram.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
public class WeatherBot extends AbilityBot {
    //Replace "..." with your Telegram bot token
    private static final String BOT_TOKEN = "6786107649:AAEdO9GEwu6TZIDIKX8EB6Y9RbFUMrICC-o";
    //Replace "..." with your Telegram bot name
    private static final String BOT_NAME = "AlsoWeatherBot";
    private WeatherParser weatherParser = new WeatherParser();

    private HashMap<Long, String> hashMap = new HashMap<Long, String>();
    public WeatherBot() {
        super(BOT_TOKEN, BOT_NAME);
    }

    @Override
    public long creatorId() {
        return 1005181492;
    }


    private SendMessage f(long chat_id)
    {
        SendMessage message = new SendMessage();

        message.setChatId(String.valueOf(chat_id));
        message.setText("Hello! Choose between the forecast or the current weather");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        //markupInline.setOneTimeKeyboard(true);

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Current Weather Data");
        inlineKeyboardButton1.setCallbackData("Current");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("5 Day / 3 Hour Forecast");
        inlineKeyboardButton2.setCallbackData("Forecast");
        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        return message;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage())
        {
            if(update.getMessage().getText().equals("/start"))
            {
                try {
                    sender.execute(f(update.getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                if(hashMap.get(update.getMessage().getChatId()).equals("Forecast"))
                {
                    try {
                        sender.execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), weatherParser.getForecastWeather(update.getMessage().getText())));
                        hashMap.remove(update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(hashMap.get(update.getMessage().getChatId()).equals("Current"))
                {
                    try {
                        sender.execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), weatherParser.getCurrentWeather(update.getMessage().getText())));
                        hashMap.remove(update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        if(update.hasCallbackQuery())
        {
            if(update.getCallbackQuery().getData().equals("Forecast"))
            {
                hashMap.put(update.getCallbackQuery().getMessage().getChatId(), "Forecast");
                 try {
                    sender.execute(new SendMessage(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "Type the text you want For example: \"New York\" or \"Istanbul\""));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(update.getCallbackQuery().getData().equals("Current"))
            {
                System.out.println("tut");
                hashMap.put(update.getCallbackQuery().getMessage().getChatId(), "Current");
                try {
                    sender.execute(new SendMessage(String.valueOf(update.getCallbackQuery().getMessage().getChatId()), "Type the text you want For example: \"New York\" or \"Istanbul\""));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

}
