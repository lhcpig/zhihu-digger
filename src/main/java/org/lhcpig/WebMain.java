package org.lhcpig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lhcpig on 2015/7/16.
 */
public class WebMain {

    public static String actor = "";
    public static long newestActivityTime = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        actor = getActor();
        Timer timer = new Timer();
        timer.schedule(task, 0, 10 * 60 * 1000);
    }

    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            String jsonResult;
            try {
                jsonResult = Jsoup.connect("http://www.zhihu.com/people/" + ConfigManager.getPeople() + "/activities")
                        .ignoreContentType(true)
                        .method(Connection.Method.POST)
                        .execute()
                        .body();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            JsonObject result = new JsonParser().parse(jsonResult).getAsJsonObject();
            if (result.get("r").getAsInt() == 0) {
                JsonElement msg = result.getAsJsonArray("msg").get(1);
                Element parse = Jsoup.parse(msg.getAsString()).body();
                List<Element> divs = parse.children()
                        .stream()
                        .filter(div -> "a".equals(div.attr("data-type")))
                        .filter(div -> div.hasAttr("data-time"))
                        .collect(Collectors.toList());

                divs.stream().filter(div -> (Long.parseLong(div.attr("data-time")) * 1000) > newestActivityTime)
                        .forEach(div -> {
                            Mail mail = buildMail(div);
                            try {
                                MailManager.sendMail(mail);
                            } catch (MessagingException e) {
                                System.out.println("fail send mail");
                            }
                        });
                Optional<Long> currentNewest = divs.stream()
                        .map(div -> Long.parseLong(div.attr("data-time")) * 1000)
                        .max(Comparator.<Long>naturalOrder());
                if (currentNewest.isPresent()) {
                    newestActivityTime = currentNewest.get();
                }

            }
        }
    };

    private static Mail buildMail(Element div) {
        Element questionA = div.getElementsByClass("question_link").get(0);
        String href = questionA.attr("href");
        String question = questionA.text();
        Element author = div.getElementsByClass("zm-item-answer-author-wrap").get(0);
        String authorName = author.child(1).text();
        boolean createAnswer = actor.equals(authorName);
        String actionStr = createAnswer ? "回答了" : "赞同了";
        String answer = div.select(".zm-item-rich-text .content").get(0).text();
        String content = "<h1><a href='http://www.zhihu.com" + href + "'>" + question + "</a></h1>" + authorName + "<br />" + answer;
        return MailManager.createMail(actor + actionStr + question, content);
    }

    private static String getActor() throws IOException {
        Document document = Jsoup.connect("http://www.zhihu.com/people/" + ConfigManager.getPeople()).get();
        return document.getElementsByClass("name").get(0).text();
    }
}
