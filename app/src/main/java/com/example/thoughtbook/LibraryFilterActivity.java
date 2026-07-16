package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class LibraryFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_filter);

        String statusName = getIntent().getStringExtra("status"); // may be null
        String shelfId = getIntent().getStringExtra("shelfId");   // may be null
        String screenTitle = getIntent().getStringExtra("title"); // shelf name, or null for status screens

        TextView title = findViewById(R.id.filterTitle);
        if (screenTitle != null) {
            title.setText(screenTitle);
        } else if (statusName != null) {
            title.setText(niceLabel(ShelfStatus.valueOf(statusName)));
        } else {
            title.setText("All Books");
        }
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        RecyclerView grid = findViewById(R.id.filteredGrid);
        grid.setLayoutManager(new GridLayoutManager(this, 3));

        BookCardAdapter adapter = new BookCardAdapter(new ArrayList<>(), book -> {
            Intent intent = new Intent(this, BookDetailActivity.class);
            intent.putExtra("bookId", book.getBookId());
            startActivity(intent);
        });
        grid.setAdapter(adapter);

//        String uid = FirebaseAuth.getInstance().getUid();
//        new BookRepository(uid).getAllBooks().observe(this, books -> {
//            List<Book> filtered = new ArrayList<>();
//            for (Book b : books) {
//                if (shelfId != null) {
//                    if (b.getShelfIds() != null && b.getShelfIds().contains(shelfId)) filtered.add(b);
//                } else {
//                    ShelfStatus filterStatus = ShelfStatus.valueOf(statusName);
//                    if (b.getStatus() == filterStatus) filtered.add(b);
//                }
//            }
//            adapter.updateBooks(filtered);
//        });
        String uid = FirebaseAuth.getInstance().getUid();
        new BookRepository(uid).getAllBooks().observe(this, books -> {
            List<Book> filtered = new ArrayList<>();
            for (Book b : books) {
                if (shelfId != null) {
                    if (b.getShelfIds() != null && b.getShelfIds().contains(shelfId)) filtered.add(b);
                } else if (statusName != null) {
                    ShelfStatus filterStatus = ShelfStatus.valueOf(statusName);
                    if (b.getStatus() == filterStatus) filtered.add(b);
                } else {
                    filtered.add(b); // no filter at all — "View All" case
                }
            }
            adapter.updateBooks(filtered);
        });
    }

    private String niceLabel(ShelfStatus status) {
        switch (status) {
            case TO_READ: return "To Read";
            case READING: return "Reading Now";
            case FINISHED: return "Finished";
            case ABANDONED: return "Abandoned";
            default: return "Books";
        }
    }
}