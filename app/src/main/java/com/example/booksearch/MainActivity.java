package com.example.booksearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.net.Uri.Builder;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {


    //Log tag for debugging purposes
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    //Getting title author and image, link book to where to buy when clicked
    private static String queryUrl = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=20&orderBy=relevance";
    private static String baseUrl = "https://www.googleapis.com/books/v1/volumes?maxResults=20&orderBy=relevance&q=";
    private EditText editText;
    private List<Book> books = new ArrayList<>();
    private static BooksListAdapter booksListAdapter;
    private static int BOOK_LOADER_ID;
    private Button searchButton;
    private TextView emptyTextView;
    private ImageView imageView;
    private ProgressBar loadProgressIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Hook up search button
        searchButton = findViewById(R.id.search_button);

        //Hook up editText
        editText = findViewById(R.id.search_edit_text);

        //setting up searchButtonOnclickListener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchButtonOnClick();
            }
        });


        //Get a connectivity Manager to monitor network state
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        //Get details about the devices network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Hook up the empty textView
        emptyTextView = findViewById(R.id.empty_text_view);

        //Hook up progress indicator
        loadProgressIndicator = findViewById(R.id.loading_data_progress_indicator);

        //If there is a network connection fetch data if not, set the text on the empty text view accordingly
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else {
            //if no internet connection display error message
            loadProgressIndicator.setVisibility(View.GONE);
            emptyTextView.setText(R.string.no_internet);
        }
        // the Book objects are added to this array list off the main thread, thats why the adapter is set to an empty array list.
        //This line of code will always be executed before the asynchonous Load is done, because it's on the main thread.
        booksListAdapter = new BooksListAdapter(this, new ArrayList<Book>());

        ListView bookListView = findViewById(R.id.book_list_view);

        bookListView.setAdapter(booksListAdapter);
        bookListView.setEmptyView(emptyTextView);

        //Set onclick listener on the list to send the user to where to buy the book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = booksListAdapter.getItem(position);

                //Check to see if there is a place to buy the book before sending the user off
                if (currentBook.getWhereToBuyBook().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cannot find where to purchase", Toast.LENGTH_SHORT).show();

                } else {
                    Uri bookURi = Uri.parse(currentBook.getWhereToBuyBook());
                    //Creating an intent to send the user to a website
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookURi);

                    //send the intent to another app that can handle it
                    startActivity(websiteIntent);

                }

            }

        });
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookLoader(this, queryUrl);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
        //After the first results load check for internet connectivity to avoid error message loading at app startup
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            emptyTextView.setText(R.string.no_internet);
        } else if (networkInfo != null && networkInfo.isConnected()) {
            //display when there is internet but there were no results
            emptyTextView.setText(R.string.no_books_found);
        }

        booksListAdapter.clear();

        //If there is a valid list of books, add them to the dataset, this will trigger the listview to update
        if (data != null && !data.isEmpty()) {
            booksListAdapter.addAll(data);
            booksListAdapter.notifyDataSetChanged();
        } else {
            emptyTextView.setText(R.string.no_books_found);
        }

        loadProgressIndicator.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        booksListAdapter.clear();
    }


    //Method that takes the current URL and modifies it based on what the user searched
    private void searchButtonOnClick() {
        Log.d(LOG_TAG, "searchButtonOnClick was called");

        //Get the user input
        if (TextUtils.isEmpty(editText.getText())) {
            Toast.makeText(this, "No Search Entered", Toast.LENGTH_SHORT).show();
        } else {
            queryUrl = baseUrl + editText.getText().toString();
            LoaderManager loaderManager = getSupportLoaderManager();
            booksListAdapter.clear();
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);

        }
    }

}
