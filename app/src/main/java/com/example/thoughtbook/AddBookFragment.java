package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
//                @Override
//                public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
//                    if (response.body() != null && response.body().items != null) {
//                        adapter.updateItems(response.body().items);
//                    } else {
//                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
//                    }
//                }

                @Override
                public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                    Log.d("SearchDebug", "HTTP code: " + response.code());
                    Log.d("SearchDebug", "Raw body: " + response.raw().toString());

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(),"Search service unavailable, try again", Toast.LENGTH_SHORT).show();
                        try {
                            Log.e("SearchDebug", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("SearchDebug", "Couldn't read error body");
                        }
                    }

                    if (response.body() != null && response.body().items != null) {
                        adapter.updateItems(response.body().items);
                    } else {
                        adapter.updateItems(new ArrayList<>());
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
        VolumeInfo info = item.volumeInfo;
        Intent intent = new Intent(getContext(), LogBookActivity.class);
        intent.putExtra("title", info.title);
        intent.putExtra("author", info.authors != null && !info.authors.isEmpty()
                ? String.join(", ", info.authors) : "Unknown author");
        intent.putExtra("coverUrl", info.imageLinks != null ? info.imageLinks.thumbnail : null);
        intent.putExtra("googleBooksId", item.id);
        intent.putExtra("genre", info.categories != null && !info.categories.isEmpty()
                ? info.categories.get(0) : null);
        startActivity(intent);
    }
}