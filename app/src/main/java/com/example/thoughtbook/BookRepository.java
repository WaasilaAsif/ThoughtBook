package com.example.thoughtbook;
import retrofit2.Callback;

import  com.example.thoughtbook.BuildConfig;
//import com.google.firebase.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import retrofit2.Callback;
public class BookRepository {
    private final FirebaseFirestore db;
    private final GoogleBooksApiService api;
    private final String uid;

    public BookRepository(String uid) {
        this.uid = uid;
        this.db = FirebaseFirestore.getInstance();
        this.api = RetrofitClient.getGoogleBooksService();
    }

    private CollectionReference booksRef() {
        return db.collection("users").document(uid).collection("books");
    }

    private CollectionReference shelvesRef() {
        return db.collection("users").document(uid).collection("shelves");
    }

    // ---------- Books ----------
    public LiveData<List<Book>> getAllBooks() {
        MutableLiveData<List<Book>> liveData = new MutableLiveData<>();
        booksRef().addSnapshotListener((snapshot, error) -> {
            if (snapshot != null) liveData.setValue(snapshot.toObjects(Book.class));
        });
        return liveData;
    }

    public void logBook(Book book) {
        booksRef().document(book.getBookId()).set(book);
    }

    public void updateStatus(String bookId, ShelfStatus status) {
        booksRef().document(bookId).update("status", status);
    }

    public void updateCurrentPage(String bookId, int page) {
        booksRef().document(bookId).update("currentPage", page);
    }

    public void addBookToShelf(String bookId, String shelfId) {
        booksRef().document(bookId).update("shelfIds", FieldValue.arrayUnion(shelfId));
    }

    public void removeBookFromShelf(String bookId, String shelfId) {
        booksRef().document(bookId).update("shelfIds", FieldValue.arrayRemove(shelfId));
    }

    // ---------- Reading logs (subcollection) ----------
    public void addLogEntry(String bookId, ReadingLogEntry entry) {
        CollectionReference logsRef = booksRef().document(bookId).collection("logs");
        logsRef.add(entry);
        // keep the book doc's currentPage + emotion in sync for card display
        booksRef().document(bookId).update(
                "currentPage", entry.pageAtLog,
                "currentEmotionColorHex", entry.emotionColorHex
        );
    }

    public LiveData<List<ReadingLogEntry>> getLogTimeline(String bookId) {
        MutableLiveData<List<ReadingLogEntry>> liveData = new MutableLiveData<>();
        booksRef().document(bookId).collection("logs")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null) liveData.setValue(snapshot.toObjects(ReadingLogEntry.class));
                });
        return liveData;
    }

    // ---------- Shelves ----------
    public void createShelf(Shelf shelf) {
        shelvesRef().document(shelf.shelfId).set(shelf);
    }

    public LiveData<List<Shelf>> getShelves() {
        MutableLiveData<List<Shelf>> liveData = new MutableLiveData<>();
        shelvesRef().orderBy("sortOrder").addSnapshotListener((snapshot, error) -> {
            if (snapshot != null) liveData.setValue(snapshot.toObjects(Shelf.class));
        });
        return liveData;
    }

    // ---------- Explore ----------


// ...

    public void getSimilarBooks(String genre, String author, Callback<GoogleBooksResponse> cb) {
        String query = "subject:" + genre + "+inauthor:" + author;
        api.searchVolumes(query, BuildConfig.GOOGLE_BOOKS_API_KEY).enqueue(cb);
    }

    public void searchBooks(String queryText, Callback<GoogleBooksResponse> cb) {
        api.searchVolumes(queryText, BuildConfig.GOOGLE_BOOKS_API_KEY).enqueue(cb);
    }
    // ---------- Pace estimate ----------
    public double estimateMinutesRemaining(Book book, List<ReadingLogEntry> logs) {
        if (logs == null || logs.size() < 2) {
            double defaultPagesPerMin = 0.6;
            return (book.getTotalPages() - book.getCurrentPage()) / defaultPagesPerMin;
        }
        ReadingLogEntry last = logs.get(logs.size() - 1);
        ReadingLogEntry prev = logs.get(logs.size() - 2);
        double minutesElapsed = (last.timestamp - prev.timestamp) / 60000.0;
        double pagesPerMin = (last.pageAtLog - prev.pageAtLog) / minutesElapsed;
        if (pagesPerMin <= 0) pagesPerMin = 0.6;
        return (book.getTotalPages() - book.getCurrentPage()) / pagesPerMin;
    }
}