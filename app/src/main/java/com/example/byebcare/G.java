package com.example.byebcare;

public class G {


    public static final int POLLING_FREQUENCY = 1000 * 10;
    public static final int EMERGENCY_CALL_DELAY = 1000 * 10;

    //Intent REQUEST_TYPE
    public static final int START_POLLING = 0;
    public static final int DO_POLLING = 1;
    public static final int STOP_POLLING = 2;
    public static final int NOTIFICATION_ACTION = 3;
    public static final int EMERGENCY_CALL = 4;

    //NOTIFICATION_TYPE
    public static final int NOTIFICATION_DEFAULT = 0;
    public static final int NOTIFICATION_EMERGENCY = 1;
    public static final int NOTIFICATION_FOREGROUND = 2;

    //NOTIFICATION_TIMEOUT
    public static final int NOTIFICATION_TIMEOUT = 1000;


    public static final int FOREGROUND_ID = 1;

    public static final String REQUEST_TYPE = "REQUEST_TYPE";

    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String CHANNEL_NAME = "CHANNEL_NAME";
    public static final String CHANNEL_DESCRIPTION = "CHANNEL_NAME";

    public static final String SERVER_URL = "http://10.4.104.131";
}
