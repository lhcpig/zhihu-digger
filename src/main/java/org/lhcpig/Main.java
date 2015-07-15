package org.lhcpig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by lhcpig on 2015/7/11.
 */
public class Main {

    private static long lastRefreshId = 1436623608;

    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        p.load(Main.class.getResourceAsStream("/config.properties"));
        String authorization = p.getProperty("Authorization");
        String people = p.getProperty("people");
        String doc = Jsoup.connect("http://api.zhihu.com/people/" + people + "/activities")
                .ignoreContentType(true)
                .header("Authorization", authorization)
                .timeout(5000)
                .execute()
                .body();


        JsonArray data = new JsonParser().parse(doc).getAsJsonObject().getAsJsonArray("data");

        List<Activity> activities = StreamSupport.stream(data.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(e -> e.getAsJsonPrimitive("id").getAsLong() > lastRefreshId)
                .map(e -> {
                    long id = e.getAsJsonPrimitive("id").getAsLong();
                    String type = e.getAsJsonPrimitive("type").getAsString();
                    String verb = e.getAsJsonPrimitive("verb").getAsString();
                    if(!"ANSWER_VOTE_UP".equals(verb) && !"ANSWER_CREATE".equals(verb)){
                        return null;
                    }
                    JsonObject target = e.getAsJsonObject("target");
                    long answerId = target.getAsJsonPrimitive("id").getAsLong();
                    JsonObject question = target.getAsJsonObject("question");
                    long questionId = question.getAsJsonPrimitive("id").getAsLong();
                    return new Activity(id, type, verb, questionId, answerId);
                })
                .filter(activity1 -> activity1 != null)
                .collect(Collectors.toList());
        if (activities.isEmpty()) {
            return;
        }

        activities.stream().forEach(activity -> System.out.println(activity.id));

        Optional<Long> max = activities.stream()
                .map(a -> a.id)
                .max(Comparator.<Long>naturalOrder());
        lastRefreshId = max.get();


    }
}
