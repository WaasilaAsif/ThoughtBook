package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BookshelfFragment extends Fragment {

    private BookshelfViewModel viewModel;
    private BookCardAdapter adapter;
    private List<Book> cachedBooks = new ArrayList<>();
    private List<Shelf> cachedShelves = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }
//    private void updateLibraryCounts(LinearLayout container, int toRead, int reading, int finished, int abandoned) {
//        container.removeAllViews();
//        addCountRow(container, "To Read", toRead, ShelfStatus.TO_READ);
//        addCountRow(container, "Reading Now", reading, ShelfStatus.READING);
//        addCountRow(container, "Finished", finished, ShelfStatus.FINISHED);
//        addCountRow(container, "Abandoned", abandoned, ShelfStatus.ABANDONED);
//    }

    private void rebuildLibrarySection(LinearLayout container) {
        container.removeAllViews();

        int toRead = 0, reading = 0, finished = 0, abandoned = 0;
        for (Book b : cachedBooks) {
            if (b.getStatus() == ShelfStatus.TO_READ) toRead++;
            else if (b.getStatus() == ShelfStatus.READING) reading++;
            else if (b.getStatus() == ShelfStatus.FINISHED) finished++;
            else if (b.getStatus() == ShelfStatus.ABANDONED) abandoned++;
        }

        addStatusRow(container, "To Read", toRead, ShelfStatus.TO_READ);
        addStatusRow(container, "Reading Now", reading, ShelfStatus.READING);
        addStatusRow(container, "Finished", finished, ShelfStatus.FINISHED);
        addStatusRow(container, "Abandoned", abandoned, ShelfStatus.ABANDONED);

        for (Shelf shelf : cachedShelves) {
            int count = 0;
            for (Book b : cachedBooks) {
                if (b.getShelfIds() != null && b.getShelfIds().contains(shelf.getShelfId())) count++;
            }
            addShelfRow(container, shelf, count);
        }
    }

    private void addStatusRow(LinearLayout container, String label, int count, ShelfStatus status) {
        LinearLayout row = buildRow(label, count);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LibraryFilterActivity.class);
            intent.putExtra("status", status.name());
            startActivity(intent);
        });
        container.addView(row);
    }

    private void addShelfRow(LinearLayout container, Shelf shelf, int count) {
        LinearLayout row = buildRow(shelf.getName(), count);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LibraryFilterActivity.class);
            intent.putExtra("shelfId", shelf.getShelfId());
            intent.putExtra("title", shelf.getName());
            startActivity(intent);
        });
        container.addView(row);
    }

    private LinearLayout buildRow(String label, int count) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 12, 0, 12);
        row.setClickable(true);

        TextView labelView = new TextView(getContext());
        labelView.setText(label);
        labelView.setTextColor(getResources().getColor(R.color.ink_primary));
        labelView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView countView = new TextView(getContext());
        countView.setText(String.valueOf(count));
        countView.setTextColor(getResources().getColor(R.color.ink_secondary));

        row.addView(labelView);
        row.addView(countView);
        return row;
    }

    private void addCountRow(LinearLayout container, String label, int count, ShelfStatus status) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 12, 0, 12);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LibraryFilterActivity.class);
            intent.putExtra("status", status.name());
            startActivity(intent);
        });

        TextView labelView = new TextView(getContext());
        labelView.setText(label);
        labelView.setTextColor(getResources().getColor(R.color.ink_primary));
        labelView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView countView = new TextView(getContext());
        countView.setText(String.valueOf(count));
        countView.setTextColor(getResources().getColor(R.color.ink_secondary));

        row.addView(labelView);
        row.addView(countView);
        container.addView(row);
    }

//    private void addCountRow(LinearLayout container, String label, int count) {
//        LinearLayout row = new LinearLayout(getContext());
//        row.setOrientation(LinearLayout.HORIZONTAL);
//        row.setPadding(0, 12, 0, 12);
//
//        TextView labelView = new TextView(getContext());
//        labelView.setText(label);
//        labelView.setTextColor(getResources().getColor(R.color.ink_primary));
//        labelView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//
//        TextView countView = new TextView(getContext());
//        countView.setText(String.valueOf(count));
//        countView.setTextColor(getResources().getColor(R.color.ink_secondary));
//
//        row.addView(labelView);
//        row.addView(countView);
//        container.addView(row);
//    }
    private void openDetail(Book book) {
        Intent intent = new Intent(getContext(), BookDetailActivity.class);
        intent.putExtra("bookId", book.getBookId());
        startActivity(intent);
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

        RecyclerView readingNowList = view.findViewById(R.id.readingNowList);
        readingNowList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        BookCardAdapter readingNowAdapter = new BookCardAdapter(new ArrayList<>(), book -> openDetail(book));
        readingNowList.setAdapter(readingNowAdapter);
//
//        LinearLayout libraryCountsContainer = view.findViewById(R.id.libraryCountsContainer);
//
//        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
//            List<Book> readingNow = new ArrayList<>();
//            int toRead = 0, reading = 0, finished = 0, abandoned = 0;
//
//            for (Book b : books) {
//                if (b.getStatus() == ShelfStatus.READING) { readingNow.add(b); reading++; }
//                else if (b.getStatus() == ShelfStatus.TO_READ) toRead++;
//                else if (b.getStatus() == ShelfStatus.FINISHED) finished++;
//                else if (b.getStatus() == ShelfStatus.ABANDONED) abandoned++;
//            }
//
//            readingNowAdapter.updateBooks(readingNow);
//            updateLibraryCounts(libraryCountsContainer, toRead, reading, finished, abandoned);
//        });

        LinearLayout libraryCountsContainer = view.findViewById(R.id.libraryCountsContainer);

        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            cachedBooks = books;

            List<Book> readingNow = new ArrayList<>();
            for (Book b : books) {
                if (b.getStatus() == ShelfStatus.READING) readingNow.add(b);
            }
            readingNowAdapter.updateBooks(readingNow);

            rebuildLibrarySection(libraryCountsContainer);
        });

        String uid = FirebaseAuth.getInstance().getUid();
        new BookRepository(uid).getShelves().observe(getViewLifecycleOwner(), shelves -> {
            cachedShelves = shelves;
            rebuildLibrarySection(libraryCountsContainer);
        });

    }
}