package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class BookshelfFragment extends Fragment {

    private BookshelfViewModel viewModel;
    private BookCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }
    private void showCreateShelfDialog() {
        EditText input = new EditText(getContext());
        input.setHint("Shelf name");

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("New Shelf")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String shelfName = input.getText().toString().trim();
                    if (shelfName.isEmpty()) {
                        Toast.makeText(getContext(), "Enter a shelf name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Shelf shelf = new Shelf();
                    shelf.setShelfId(java.util.UUID.randomUUID().toString());
                    shelf.setName(shelfName);
                    shelf.setDateCreated(System.currentTimeMillis());
                    shelf.setSortOrder(0);

                    String uid = FirebaseAuth.getInstance().getUid();
                    new BookRepository(uid).createShelf(shelf);

                    Toast.makeText(getContext(), "Shelf created!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.addShelfButton).setOnClickListener(v -> showCreateShelfDialog());
        viewModel = new ViewModelProvider(this).get(BookshelfViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.bookGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new BookCardAdapter(new java.util.ArrayList<>(), book -> {
            Intent intent = new Intent(getContext(), BookDetailActivity.class);
            intent.putExtra("bookId", book.getBookId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            adapter.updateBooks(books);
        });


    }
}