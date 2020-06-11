package com.codingwithmitch.foodrecipes.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class Converters {

    private static final String TAG = "Converters";

    public static byte[] convertImageToByteArray(String path) {

        Bitmap bitmap = null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            Log.d(TAG, "getImageAsByteArray: reached try block");

        } catch (MalformedURLException e) {
            Log.d(TAG, "getImageAsByteArray: MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "getImageAsByteArray: IOException ");
            e.printStackTrace();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY_IN_DB, stream);

        Log.d(TAG, "getImageAsByteArray: "+ stream.toByteArray().length);

        return stream.toByteArray();

    }

    public static Bitmap getImageFromByteArray(byte[] array) {
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static char[][] convertStringArrayToMultiCharArray(String[] ingredients) {
        char[][] array = new char[ingredients.length][];
        for (int i = 0; i < ingredients.length; i++) {
            array[i] = ingredients[i].toCharArray();
        }
        return array;
    }

    public static String[] convertMultiCharArrayToStringArray(char[][] ingredients) {
        String[] array = new String[ingredients.length];
                for (int i = 0; i < ingredients.length; i++) {
            array[i] = new String(ingredients[i]);
        }
        return array;
    }
}