import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.toIntExact;

public class PirateBayBot extends TelegramLongPollingBot{
    // PB = Pirate Bay

    String[] urls = {
            "https://thepiratebay.myunblock.com/s/?q="
    };

    Map<Long, List<Torrent>> map = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        new Thread(() -> {
            if (update.hasMessage() && update.getMessage().hasText())
            {

                if (update.getMessage().getText().equals("/start"))
                {

                    try {
                        sendMsg("Hello! Just type anything you want to download from Pirate Bay!", update.getMessage().getChatId());
                        sendMsg("New user registered: " + update.getMessage().getChatId(), 34540125);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else if (update.getMessage().getText().split(" ")[0].equals("/send"))
                {
                  /*
                      That's how I notify users from bot, using /send command
                      ex: /send 0071337 Hello, there is new update
                  */

                    String[] strs = update.getMessage().getText().split(" ");

                    int id = Integer.parseInt(strs[1]);

                    String msg = "";

                    for (int i = 2; i < strs.length; i++) {
                        msg += strs[i] + " ";
                    }

                    if (update.getMessage().getChatId() == 34540125) {
                        try {
                            sendMsg(msg, id);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                  /* I use thread so there will be separate search for each user
                     Otherwise bot won't serve users simultaneously
                  */

                    Document doc = null;
                    List <Torrent> torrents = new ArrayList<>();

                    try {
                        sendMsg("Searching...", update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    String clientUrl = update.getMessage().getText();

                    try {
                        // I get notification about a user searching for torrent
                        sendMsg(update.getMessage().getChatId() + " is looking for: " + clientUrl, 34540125);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    // result is url where bot sends request, PB's address, user's request and page
                    String result = urls[0] + clientUrl.replace(" ", "+") + "&page=0&orderby=99";

                    // but there are different url's for top sections
                    switch (clientUrl) {
                        case "/topaudio":
                            result = "https://thepiratebay.myunblock.com/top/100";
                            break;
                        case "/topvideo":
                            result = "https://thepiratebay.myunblock.com/top/200";
                            break;
                        case "/topapps":
                            result = "https://thepiratebay.myunblock.com/top/300";
                            break;
                        case "/topgames":
                            result = "https://thepiratebay.myunblock.com/top/400";
                            break;
                        case "/topother":
                            result = "https://thepiratebay.myunblock.com/top/600";
                            break;
                    }

                    try {
                        doc = Jsoup.connect(result)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .referrer("http://www.google.com")
                                .ignoreHttpErrors(true)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We get all the torrents displayed on the page
                    Elements elements = doc.select("#main-content #searchResult tbody tr td");


                    for (int i = 0; i < elements.size(); i++)
                    {
                        // We check if it's a torrent element, not other column or row
                        if (elements.get(i).attr("class").equals("") && elements.get(i).attr("align").equals("")) {
                            Torrent torrent = new Torrent();
                            torrent.setName(elements.get(i).select(".detName a").text());
                            torrent.setDesc(elements.get(i).select(".detDesc").text());
                            torrent.setMagnet(elements.get(i).select("a").get(1).attr("href"));

                            // for some reason there can be empty objects, so I check it
                            if (!torrent.getName().equals(""))
                            {
                                torrents.add(torrent);
                            }

                            map.put(update.getMessage().getChatId(), torrents);
                        }
                    }

                    System.out.println(torrents.size());

                    SendMessage message = new SendMessage() // Create a message object
                            .setChatId(update.getMessage().getChatId())
                            .setText("That's what we got");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

                    for (Torrent torrent : torrents) {
                        List<InlineKeyboardButton> rowInline = new ArrayList<>();

                        rowInline.add(new InlineKeyboardButton().setText(torrent.getName()).setCallbackData(torrent.getKey()));
                        rowList.add(rowInline);
                    }

                    // Set the keyboard to the markup
                    // Add it to the message
                    markupInline.setKeyboard(rowList);
                    message.setReplyMarkup(markupInline);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        message.setText("Temporarily unavailable");
                        message.setReplyMarkup(null);
                        try {
                            execute(message);
                        } catch (TelegramApiException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (torrents.size() == 0) {
                        try {
                            sendMsg("Unable to find anything. Try to make more understandable request.\n" +
                                    "If you are search for a film, don't type 'subtitles', it is gonna return you nothing.\n" +
                                    "Choose films with subtitles from all found films.", update.getMessage().getChatId());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else if (update.hasCallbackQuery())
            {
                String call_data = update.getCallbackQuery().getData();
                long message_id = update.getCallbackQuery().getMessage().getMessageId();
                long chat_id = update.getCallbackQuery().getMessage().getChatId();

                List<Torrent> _torrents = map.get(chat_id);


                for (Torrent _torrent : _torrents)
                {
                    if (call_data.equals(_torrent.getKey()))
                    {
                        String answer = bold(_torrent.getName()) + "\n\n" + bold(_torrent.getDesc()) + "\n\n" + link(_torrent.getMagnet());
                        EditMessageText new_message = new EditMessageText()
                                .setChatId(chat_id)
                                .setMessageId(toIntExact(message_id))
                                .enableMarkdown(true)
                                .setText(answer);
                        try {
                            execute(new_message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        map.remove(update.getMessage().getChatId());
                    }
                }
            }
        }).start();
    }

    void sendMsg(String msg, long id) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setChatId(id)
                .enableMarkdown(true)
                .setText(msg);

        execute(message);
    }

    String bold(String str)
    {
        return "*" + str + "*";
    }

    String link(String str)
    {
        return "`" + str + "`";
    }

    @Override
    public String getBotUsername() {
        return "The Pirate Bay";
    }

    @Override
    public String getBotToken() {
        return "token";
    }
}
