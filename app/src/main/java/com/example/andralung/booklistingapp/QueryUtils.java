package com.example.andralung.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;


public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    public static ArrayList<Book> extractFeatureFromJson(String bookJSON) {

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<>();
        String noContent = "This book has no %s.";
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {

                JSONObject chosenBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = chosenBook.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                String authors = String.format(noContent, "authors");
                if (volumeInfo.has("authors")) {
                    authors = (String) volumeInfo.getJSONArray("authors").get(0);
                }

                String description =  String.format(noContent, "description");
                if (volumeInfo.has("description")) {
                    description = volumeInfo.getString("description");
                }

                Book selectedBook = new Book(title, authors, description);
                books.add(selectedBook);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the results", e);
        }
        return books;
    }

    public static String readFromStream(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}
