package com.example.thoughtbook;

import java.util.ArrayList;
import java.util.List;

public class Book {
    String bookId;
    String googleBooksId;
    String title;
    List<String> authors;
    String coverUrl;
    ShelfStatus status;
    List<String> shelfIds;
    int totalPages;
    int currentPage;
    String currentEmotionColorHex; // most recent log's color, shown on card
    float personalRating;
    long dateAdded;
    long dateFinished;
    // logs are a subcollection, not a field — loaded separately

    //When finished add a note apart from the lofs displays the latest log if left empty
    String note;
    //Final tags for the book all the emotional tags or the final tags after completing book

    boolean finishBook;
    ArrayList<String> EmotionTags = new ArrayList<>();




}

