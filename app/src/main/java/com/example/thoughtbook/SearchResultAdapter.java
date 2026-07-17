package com.example.thoughtbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        ImageView cover;
        TextView title;
        TextView author;

        public ViewHolder(View view) {
            super(view);
            cover = view.findViewById(R.id.resultCover);
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
        if (info.imageLinks != null && info.imageLinks.thumbnail != null) {
            Glide.with(holder.itemView.getContext())
                    .load(info.imageLinks.thumbnail.replace("http://", "https://"))
                    .into(holder.cover);
        } else {
            holder.cover.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> listener.onResultClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}