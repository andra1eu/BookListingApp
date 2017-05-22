package com.example.andralung.booklistingapp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextInputLayout searchLayout;
    private EditText searchView;
    private View button;
    private ArrayList<Book> books;
    private BookAdapter adapter;
    private ListView listViewBook;
    ImageView imageView;

    private static final String STATE_LIST = "State Adapter Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            books = savedInstanceState.getParcelableArrayList(STATE_LIST);
        } else {
            books = new ArrayList<>();
        }

        imageView = (ImageView) findViewById(R.id.image_view);
        searchLayout = (TextInputLayout) findViewById(R.id.text_layout);
        searchView = (EditText) findViewById(R.id.search_view);
        button = findViewById(R.id.search_button);
        button.setOnClickListener(this);

        listViewBook = (ListView) findViewById(R.id.list);
        adapter = new BookAdapter(this, books);
        listViewBook.setAdapter(adapter);

        searchView.addTextChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, books);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodService = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodService.hideSoftInputFromWindow(v.getWindowToken(), 0);
        searchLayout.setError(null);

        String search = searchView.getText().toString();
        URL url = null;
        try {
            String encodedSearch = URLEncoder.encode(search, "UTF-8");
            url = new URL(SEARCH_URL + encodedSearch);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (search.isEmpty() || url == null) {
            searchLayout.setError(getString(R.string.activity_main_search_text_error));
            adapter.clear();
            imageView.setImageResource(R.drawable.book_coffee);
            return;
        }


        Log.d(TAG, url.toString() + "");
        GoogleBookApiTask task = new GoogleBookApiTask();
        task.execute(url);
    }

    public class GoogleBookApiTask extends AsyncTask<URL, Void, ArrayList<Book>> {


        @Override
        protected ArrayList<Book> doInBackground(URL... params) {
            URL url = params[0];
            if (url == null) return null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /* milliseconds */);
                connection.setConnectTimeout(15000 /* milliseconds */);
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                    String jsonResponse = QueryUtils.readFromStream(inputStream);
                    ArrayList<Book> books = QueryUtils.extractFeatureFromJson(jsonResponse);
                    return books;
                } else {
                    final int responseCode = connection.getResponseCode();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchLayout.setError("Error response code: " + responseCode);
                        }
                    });
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            MainActivity.this.books = books;
            adapter.clear();
            if (books != null && !books.isEmpty()) {
                adapter.addAll(books);
            }

            if (books == null) {
                searchLayout.setError("No result found.");
                imageView.setImageResource(R.drawable.book_coffee);
                return;
            }

            if (books.isEmpty()) {
                searchLayout.setError("No result found.");
                imageView.setImageResource(R.drawable.book_coffee);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (adapter != null) adapter.clear();
        imageView.setImageDrawable(new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.colorBackground)));
    }
}
