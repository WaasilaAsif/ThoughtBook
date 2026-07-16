
        package com.example.thoughtbook;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String bookId;
    private String googleBooksId;
    private String title;
    private List<String> authors;
    private String coverUrl;
    private ShelfStatus status;
    private List<String> shelfIds;
    private int totalPages;
    private int currentPage;
    private String currentEmotionColorHex; // most recent log's color, shown on card
    private float personalRating;
    private long dateAdded;
    private long dateFinished;

    // logs are a subcollection, not a field — loaded separately

    //When finished add a note apart from the lofs displays the latest log if left empty
    private String note;

    //Final tags for the book all the emotional tags or the final tags after completing book
    private List<String> emotionTags = new ArrayList<>();

    // Required empty constructor for Firestore
    public Book() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getGoogleBooksId() {
        return googleBooksId;
    }

    public void setGoogleBooksId(String googleBooksId) {
        this.googleBooksId = googleBooksId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public ShelfStatus getStatus() {
        return status;
    }

    public void setStatus(ShelfStatus status) {
        this.status = status;
    }

    public List<String> getShelfIds() {
        return shelfIds;
    }

    public void setShelfIds(List<String> shelfIds) {
        this.shelfIds = shelfIds;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getCurrentEmotionColorHex() {
        return currentEmotionColorHex;
    }

    public void setCurrentEmotionColorHex(String currentEmotionColorHex) {
        this.currentEmotionColorHex = currentEmotionColorHex;
    }

    public float getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(float personalRating) {
        this.personalRating = personalRating;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(long dateFinished) {
        this.dateFinished = dateFinished;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getEmotionTags() {
        return emotionTags;
    }

    public void setEmotionTags(List<String> emotionTags) {
        this.emotionTags = emotionTags;
    }

    private String genre;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}

