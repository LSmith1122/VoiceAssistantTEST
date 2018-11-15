package com.example.lsmith18.mytestapplication.data;

import java.util.Arrays;
import java.util.List;

public class RequestActionsUtils {

    public static final List<String> ACTION_LIST_AWAKENINGS = Arrays.asList(
            "Hey Amrock",
            "Hey am rock"
    );

    public static final List<String> ACTION_LIST_LOCATE = Arrays.asList(
            "where",
            "show me where",
            "show me a map",
            "where am I",
            "where are we");
    public static final List<String> ACTION_LIST_QUERY = Arrays.asList(
            "what is",
            "what are",
            "show me");

    public static final String ACTION_LOCATE = "geo:0,0?q=";
    public static final String ACTION_QUERY = "http://www.google.com/#q=";

    public static final List<List<String>> ACTION_COMPLETE_LIST = Arrays.asList(
            ACTION_LIST_LOCATE,
            ACTION_LIST_QUERY);
}
