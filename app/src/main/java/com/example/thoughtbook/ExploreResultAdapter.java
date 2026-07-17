package com.example.thoughtbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ExploreResultAdapter extends RecyclerView.Adapter<ExploreResultAdapter.ViewHolder> {

    public interface OnResultActionListener {
        void onCardClick(Item item);
        void onQuickAddClick(Item item);
    }

    private List<Item> items;
    private final OnResultActionListener listener;

    public ExploreResultAdapter(List<Item> items, OnResultActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, author, category, description;
        Button addTbrButton;

        public ViewHolder(View view) {
            super(view);
            cover = view.findViewById(R.id.exploreCover);
            title = view.findViewById(R.id.exploreTitle);
            author = view.findViewById(R.id.exploreAuthor);
            category = view.findViewById(R.id.exploreCategory);
            description = view.findViewById(R.id.exploreDescription);
            addTbrButton = view.findViewById(R.id.addTbrButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        VolumeInfo info = item.volumeInfo;

        holder.title.setText(info.title != null ? info.title : "Untitled");
        holder.author.setText(info.authors != null && !info.authors.isEmpty()
                ? String.join(", ", info.authors) : "Unknown author");

        if (info.categories != null && !info.categories.isEmpty()) {
            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText(info.categories.get(0));
        } else {
            holder.category.setVisibility(View.GONE);
        }

        holder.description.setText(info.description != null ? info.description : "");

        if (info.imageLinks != null && info.imageLinks.thumbnail != null) {
            Glide.with(holder.itemView.getContext())
                    .load(info.imageLinks.thumbnail.replace("http://", "https://"))
                    .into(holder.cover);
        } else {
            holder.cover.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> listener.onCardClick(item));
        holder.addTbrButton.setOnClickListener(v -> listener.onQuickAddClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}