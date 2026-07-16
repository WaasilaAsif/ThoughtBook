package com.example.thoughtbook;
import android.util.Log;

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
import retrofit2.Call;
import retrofit2.Response;
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

    public void addLogEntry(String bookId, ReadingLogEntry entry) {
        booksRef().document(bookId).collection("logs").document(entry.logId).set(entry);
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


    public void getSimilarBooks(String genre, String author, Callback<GoogleBooksResponse> cb) {
        String query = "subject:" + genre + "+inauthor:" + author;
        Log.d("ExploreDebug", "First query: " + query);

        api.searchVolumes(query, BuildConfig.GOOGLE_BOOKS_API_KEY).enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                Log.d("ExploreDebug", "First query HTTP code: " + response.code());

                boolean genreQueryEmpty = !response.isSuccessful()
                        || response.body() == null
                        || response.body().items == null
                        || response.body().items.isEmpty();

                Log.d("ExploreDebug", "genreQueryEmpty: " + genreQueryEmpty);

                if (genreQueryEmpty) {
                    Log.d("ExploreDebug", "Falling back to: inauthor:" + author);
                    api.searchVolumes("inauthor:" + author, BuildConfig.GOOGLE_BOOKS_API_KEY).enqueue(new Callback<GoogleBooksResponse>() {
                        @Override
                        public void onResponse(Call<GoogleBooksResponse> call2, Response<GoogleBooksResponse> response2) {
                            Log.d("ExploreDebug", "Fallback query HTTP code: " + response2.code());
                            cb.onResponse(call2, response2);
                        }

                        @Override
                        public void onFailure(Call<GoogleBooksResponse> call2, Throwable t) {
                            Log.e("ExploreDebug", "Fallback query failed: " + t.getMessage());
                            cb.onFailure(call2, t);
                        }
                    });
                } else {
                    cb.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                Log.e("ExploreDebug", "First query failed: " + t.getMessage());
                cb.onFailure(call, t);
            }
        });
    }
    public void searchBooks(String queryText, Callback<GoogleBooksResponse> cb) {
        api.searchVolumes(queryText, BuildConfig.GOOGLE_BOOKS_API_KEY).enqueue(cb);
    }
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

    public LiveData<Book> getBook(String bookId) {
        MutableLiveData<Book> liveData = new MutableLiveData<>();
        booksRef().document(bookId).addSnapshotListener((snapshot, error) -> {
            if (snapshot != null && snapshot.exists()) {
                liveData.setValue(snapshot.toObject(Book.class));
            }
        });
        return liveData;
    }
    public void updateLogEntry(String bookId, String logId, ReadingLogEntry entry) {
        booksRef().document(bookId).collection("logs").document(logId).set(entry);
    }

    public void deleteLogEntry(String bookId, String logId) {
        booksRef().document(bookId).collection("logs").document(logId).delete();
    }

}