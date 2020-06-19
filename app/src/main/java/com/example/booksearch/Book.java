package com.example.booksearch;

public class Book {

    private String title;
    private String author;
    private String whereToBuyBook;

    public Book(String title, String author, String whereToBuyBook) {
        this.title = title;
        this.author = author;
        this.whereToBuyBook = whereToBuyBook;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }


    public String getWhereToBuyBook() {
        return whereToBuyBook;
    }
}
