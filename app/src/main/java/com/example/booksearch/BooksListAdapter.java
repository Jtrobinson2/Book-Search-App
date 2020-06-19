package com.example.booksearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksListAdapter extends ArrayAdapter<Book> {
    private Context context = getContext();

    public BooksListAdapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false
            );
        }

        //Find the book object at that position
        Book currentBook = getItem(position);

        //Setting the textViews of the list item to the data from the earthQuake obj
        TextView authorTextView = listItemView.findViewById(R.id.book_author_text_view);
        authorTextView.setText(currentBook.getAuthor());

        TextView bookTitleTextView = listItemView.findViewById(R.id.book_title_text_view);
        bookTitleTextView.setText(currentBook.getTitle());


        return listItemView;


    }
}
