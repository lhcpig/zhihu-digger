package org.lhcpig;

/**
 * Created by liuhengchong on 2015/7/15.
 */
public class Activity {
    public final long id;
    public final String type;
    public final String verb;
    public final long questionId;
    public final long answerId;

    public Activity(long id, String type, String verb, long questionId, long answerId) {
        this.id = id;
        this.type = type;
        this.verb = verb;
        this.questionId = questionId;
        this.answerId = answerId;
    }


}
