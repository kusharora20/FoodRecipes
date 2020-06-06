package com.codingwithmitch.foodrecipes.util;
import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public static final String BASE_URL = "https://recipesapi.herokuapp.com";
    public static final String API_KEY = "";
    public static final int NETWORK_TIMEOUT = 3000; // 3000 miliseconds or 3 seconds
    public static final Map<Integer, String> categories = new HashMap<>();
    static final int IMAGE_QUALITY_IN_DB = 100;

    static{
        System.out.println("constants class called!!!");
        categories.put(0, "breakfast");
        categories.put(1, "lunch");
        categories.put(2, "dinner");
        categories.put(3, "vegetarian");
        categories.put(4, "salad");
        categories.put(5, "pancake");
        categories.put(6, "curry");
        categories.put(7, "egg");
    }

    public static final long RECIPE_REFRESH_TIME = 30 * 24 * 60 * 60; // 30 days time in seconds
}
