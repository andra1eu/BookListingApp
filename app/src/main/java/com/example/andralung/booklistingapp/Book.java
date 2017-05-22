package com.example.andralung.booklistingapp;


import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private final String title;
    private final String authors;
    private final String description;


    public Book(String title, String authors, String description) {
        this.title = title;
        this.authors = authors;
        this.description = description;
    }

    public Book(Parcel in) {
        this.title = in.readString();
        this.authors = in.readString();
        this.description = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(authors);
        dest.writeString(description);
    }
}
