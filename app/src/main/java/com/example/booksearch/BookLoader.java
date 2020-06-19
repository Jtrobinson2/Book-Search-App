package com.example.booksearch;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    public static final String LOG_TAG = BookLoader.class.getSimpleName();
    private String url;

    public BookLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        if (url == null) {
            return null;
        }

        List<Book> fetchedBooks;
        fetchedBooks = NetworkingUtils.fetchBookData(url);
        return fetchedBooks;
    }
}
