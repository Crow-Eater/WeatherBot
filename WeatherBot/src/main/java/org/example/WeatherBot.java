package org.example;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.telegram.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
public class WeatherBot extends AbilityBot {
    //Replace "..." with your Telegram bot token
    private static final String BOT_TOKEN = "6786107649:AAEdO9GEwu6TZIDIKX8EB6Y9RbFUMrICC-o";
    //Replace "..." with your Telegram bot name
    private static final String BOT_NAME = "AlsoWeatherBot";
    private WeatherParser weatherParser = new WeatherParser();

    public WeatherBot() {
        super(BOT_TOKEN, BOT_NAME);
    }

    @Override
    public long creatorId() {
        return 1005181492;
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello! Enter the city in chat and get 5 days forecast! " +
                        "For example: \"New York\" or \"Istanbul\"", ctx.chatId()))
                .build();
    }

    public Ability sendWeather() throws MalformedURLException, IOException, com.fasterxml.jackson.core.JsonProcessingException {
        return Ability.builder()
                .name(DEFAULT)
                .flag(TEXT)
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action((MessageContext ctx) -> {
                    if (ctx.firstArg().equals(ctx.secondArg()))  {
                        silent.send(weatherParser.getWeather(ctx.firstArg()), ctx.chatId());
                    } else {
                        silent.send(weatherParser.getWeather(String.format("%s %s", ctx.firstArg(), ctx.secondArg())), ctx.chatId());
                    }
                })
                .build();


    }
}
