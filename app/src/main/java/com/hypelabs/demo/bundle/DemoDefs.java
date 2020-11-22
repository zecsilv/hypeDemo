package com.hypelabs.demo.bundle;

public class DemoDefs {

    public static final String IDENTIFIER_KEY = "id";

    public static final int ALARM_IDENTIFIER = 1;

    public static final int CHAT_IDENTIFIER = 2;

    public static final int METRICS_IDENTIFIER = 3;

    public static final String ALARM_SOUND_KEY = "asv";

    public static final String MESSAGE_CONTENT_KEY = "content";

    public static final String MESSAGE_TYPE_KEY = "message_type";

    public static final String getAlarmIdentifierString() {
        return String.valueOf(ALARM_IDENTIFIER);
    }

    public static final String getChatIdentifierString() {
        return String.valueOf(CHAT_IDENTIFIER);
    }


    public final static String HOME_TAG = "HOME_FRAG";
    public final static String CONTACT_TAG = "CONTACT_FRAG";
    public final static String MESSAGE_TAG = "MESSAGE_FRAG";
    public final static String ALARM_TAG = "ALARM_FRAG";
    public final static String METRIC_TAG = "METRIC_FRAG";
}
