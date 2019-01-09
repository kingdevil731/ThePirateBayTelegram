import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PirateBayBot extends TelegramLongPollingBot
{
    String[] urls = {"https://thepiratebay.myunblock.com/s/?q="};

    @Override
    public void onUpdateReceived(Update update)
    {
        if (update.hasMessage() && update.getMessage().hasText())
        {
            if(update.getMessage().getText().equals("/start"))
            {
                try {
                    sendMsg("Hello! Just type anything you want to download from Pirate Bay!", update.getMessage().getChatId());
                    sendMsg("New user registered: " + update.getMessage().getChatId(), 34540125);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if(update.getMessage().getText().equals("/donate"))
            {
                try {
                    sendMsg("Unfortunately, only cryptocurrencies available\n\n" +
                            "Bitcoin: `1D2NDVjioKiMntQpkqdHF1KjMH4CjM65Fj`\n" +
                            "\n" +
                            "Litecoin: `LVjYXuLTtEd3N7MULrtuRoUTUdjrMFS93k`\n" +
                            "\n" +
                            "Dogecoin: `DMBhZiqVeR2Eywgsp39VWAZPy1cxGz42XH`\n" +
                            "\n" +
                            "Ethereum: `0xe9C004AE90F4fCBAF74b122732BE334c5517eacF`\n" +
                            "\n" +
                            "Ethereum Classic: `0xe9C004AE90F4fCBAF74b122732BE334c5517eacF`\n" +
                            "\n" +
                            "Zcash: `t1bCmPzEQBpNTQShhnHGTC2sFhiBrhthdFv`\n" +
                            "\n" +
                            "Stellar: `GBD3VKBL2OOF42D4QAOAVYZ4LOSD6ULFHD64IKF5LNNS4AM5JXMS6HN7`" +
                            "\n\nThanks a lot!!", update.getMessage().getChatId());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                new Thread(() -> {
                    Document doc = null;
                    List<Torrent> torrents = new ArrayList<>();

                    try {
                        sendMsg("Searching...", update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    String clientUrl = update.getMessage().getText();

                    String result = urls[0] + clientUrl.replace(" ", "+") + "&page=0&orderby=99";

                    try {
                        doc = Jsoup.connect(result)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .referrer("http://www.google.com")
                                .ignoreHttpErrors(true)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Elements elements = doc.select("#main-content #searchResult tbody tr td");

                    for(Element element: elements)
                    {
                        if(element.attr("class").equals("") && element.attr("align").equals(""))
                        {
                            Torrent torrent = new Torrent();
                            torrent.setName(element.select(".detName a").text());
                            torrent.setDesc(element.select(".detDesc").text());
                            torrent.setMagnet(element.select("a").get(1).attr("href"));
                            if (!torrent.getName().equals("")) torrents.add(torrent);
                        }
                    }

                    for (Torrent torrent: torrents)
                    {
                        SendMessage message = new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText("*" + torrent.getName() + "\n\n" + torrent.getDesc() + "\n\n" + "*" + "`" + torrent.getMagnet() + "`")
                                .enableMarkdown(true);

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        }
    }

    void sendMsg(String msg, long id) throws TelegramApiException
    {
        SendMessage message = new SendMessage()
                .setChatId(id)
                .enableMarkdown(true)
                .setText(msg);

        execute(message);
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