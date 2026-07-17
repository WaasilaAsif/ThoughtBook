package com.example.thoughtbook;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BookshelfViewModel extends ViewModel {
    private BookRepository repository;
    private final MutableLiveData<List<Book>> books = new MutableLiveData<>(new ArrayList<>());

    public BookshelfViewModel() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            initRepository(uid);
        } else {
            // sign-in hasn't completed yet — wait for it instead of crashing
            FirebaseAuth.getInstance().addAuthStateListener(auth -> {
                String freshUid = auth.getUid();
                if (freshUid != null && repository == null) {
                    initRepository(freshUid);
                }
            });
        }
    }

    private void initRepository(String uid) {
        repository = new BookRepository(uid);
        repository.getAllBooks().observeForever(books::setValue);
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }
}