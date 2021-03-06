package org.lhcpig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lhcpig on 2015/7/16.
 */
public class WebMain {

    public static List<Person> actors = Collections.emptyList();

    public static void main(String[] args) throws IOException {
        actors = getActors();
        System.out.println("start zhihu monitor:" + actors);
        Timer timer = new Timer();
        timer.schedule(task, 0, 60 * 1000);
    }

    private static TimerTask task = new TimerTask() {
        @Override
        public void run() {
            for (Person person : actors) {
                try {
                    monitor(person);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("finish this task:" + Instant.now());
            }
        }
    };

    private static void monitor(Person person) {
        String jsonResult = getActivities(person);
        if (jsonResult == null) {
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

            divs.stream().filter(div -> (Long.parseLong(div.attr("data-time")) * 1000) > person.newestUpdateTime)
                    .forEach(div -> sendMail(div, person));

            Optional<Long> currentNewest = divs.stream()
                    .map(div -> Long.parseLong(div.attr("data-time")) * 1000)
                    .max(Comparator.<Long>naturalOrder());
            if (currentNewest.isPresent()) {
                person.newestUpdateTime = currentNewest.get();
            }
        }
    }

    private static String getActivities(Person person) {
        for (int i = 0; i < 3; i++) {
            try {
                return Jsoup.connect("http://www.zhihu.com/people/" + person.name + "/activities")
                        .ignoreContentType(true)
                        .method(Connection.Method.POST)
                        .execute()
                        .body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void sendMail(Element div, Person person) {
        Elements titleDivs = div.getElementsByClass("zm-profile-activity-page-item-main");
        String title = titleDivs.get(0).text();
        if (title.startsWith("知乎用户")) {
            title = title.replaceFirst("知乎用户", person.nickName);
        }
        Element questionA = div.getElementsByClass("question_link").get(0);
        String href = questionA.attr("href");
        String question = questionA.text();
        Element author = div.getElementsByClass("zm-item-answer-author-wrap").get(0);
        String authorName;
        if (title.split(" ")[1].startsWith("回答了")) {
            authorName = person.nickName;
        } else {
            if (author.children().size() <= 1) {//开启了隐私限制，无法判断是赞同还是回答
                authorName = "知乎用户";
            } else {
                authorName = author.child(1).text();
            }
        }
        String answer = div.select(".zm-item-rich-text .content").get(0).text();
        String content = "<h4><a href='http://www.zhihu.com" + href + "'>" + question + "</a></h4>" + authorName + "<br />" + answer;
        MailManager.instance.send(title, content);
    }

    private static List<Person> getActors() throws IOException {
        List<String> people = ConfigManager.getPeople();
        return people.stream().map(s -> new Person(s, System.currentTimeMillis(), getActor(s))).filter(p -> p.nickName != null).collect(Collectors.toList());
    }

    public static String getActor(String person) {
        Document document;
        try {
            document = Jsoup.connect("http://www.zhihu.com/people/" + person).get();
        } catch (IOException e) {
            return null;
        }
        Elements name = document.getElementsByClass("name");
        if (name.isEmpty()) {
            return "unkown";
        }
        return name.get(0).text();
    }

    private static class Person {
        String name;
        String nickName;
        long newestUpdateTime;

        public Person(String name, long newestUpdateTime, String nickName) {
            this.name = name;
            this.newestUpdateTime = newestUpdateTime;
            this.nickName = nickName;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", newestUpdateTime=" + newestUpdateTime +
                    '}';
        }
    }
}
