package com.example.thoughtbook;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BookshelfViewModel extends ViewModel {
    private final BookRepository repository;
    private final LiveData<List<Book>> books;

    public BookshelfViewModel() {
        String uid = FirebaseAuth.getInstance().getUid();
        repository = new BookRepository(uid);
        books = repository.getAllBooks();
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }
}