package com.example.thoughtbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    public interface OnResultClickListener {
        void onResultClick(Item item);
    }

    private List<Item> items;
    private final OnResultClickListener listener;

    public SearchResultAdapter(List<Item> items, OnResultClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.resultTitle);
            author = view.findViewById(R.id.resultAuthor);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        VolumeInfo info = item.volumeInfo;

        holder.title.setText(info.title != null ? info.title : "Untitled");
        holder.author.setText(info.authors != null && !info.authors.isEmpty()
                ? String.join(", ", info.authors) : "Unknown author");

        holder.itemView.setOnClickListener(v -> listener.onResultClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}