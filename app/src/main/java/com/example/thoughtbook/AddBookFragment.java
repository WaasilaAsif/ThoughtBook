package com.example.thoughtbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookFragment extends Fragment {

    private BookRepository repository;
    private SearchResultAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String uid = FirebaseAuth.getInstance().getUid();
        repository = new BookRepository(uid);

        EditText searchInput = view.findViewById(R.id.searchInput);
        Button searchButton = view.findViewById(R.id.searchButton);
        RecyclerView resultsView = view.findViewById(R.id.searchResults);

        resultsView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchResultAdapter(new ArrayList<>(), item -> openLogScreen(item));
        resultsView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (query.isEmpty()) return;

            repository.searchBooks(query, new Callback<GoogleBooksResponse>() {
                @Override
                public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                    if (response.body() != null && response.body().items != null) {
                        adapter.updateItems(response.body().items);
                    } else {
                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Search failed: check internet", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void openLogScreen(Item item) {
        // next step: pass this item's data into a LogBookActivity
        Toast.makeText(getContext(), "Selected: " + item.volumeInfo.title, Toast.LENGTH_SHORT).show();
    }
}