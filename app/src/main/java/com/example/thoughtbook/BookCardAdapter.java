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

public class BookCardAdapter extends RecyclerView.Adapter<BookCardAdapter.ViewHolder> {
    public interface onBookClickListener {
        void onBookClick(Book book);
    }
    private List<Book> books;
    private final onBookClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView fallbackTitle;
        TextView bookTitle;
        TextView paceText;

        public ViewHolder(View view) {
            super(view);
            coverImage = view.findViewById(R.id.coverImage);
            fallbackTitle = view.findViewById(R.id.fallbackTitle);
            bookTitle = view.findViewById(R.id.bookTitle);
            paceText = view.findViewById(R.id.paceText);
        }
    }

    public BookCardAdapter(List<Book> books, onBookClickListener listener) {
        this.books = books;
        this.listener = listener;
    }
    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_book_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Book book = books.get(position);

        viewHolder.bookTitle.setText(book.getTitle());
        viewHolder.itemView.setOnClickListener(v-> listener.onBookClick(book));
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            viewHolder.coverImage.setVisibility(View.VISIBLE);
            viewHolder.fallbackTitle.setVisibility(View.GONE);
            Glide.with(viewHolder.itemView.getContext())
                    .load(book.getCoverUrl())
                    .into(viewHolder.coverImage);
        } else {
            viewHolder.coverImage.setVisibility(View.GONE);
            viewHolder.fallbackTitle.setVisibility(View.VISIBLE);
            viewHolder.fallbackTitle.setText(book.getTitle());
            if (book.getCurrentEmotionColorHex() != null) {
                viewHolder.fallbackTitle.setBackgroundColor(
                        android.graphics.Color.parseColor(book.getCurrentEmotionColorHex()));
            }
        }

        viewHolder.paceText.setText(""); // pace text wired in later, once estimateMinutesRemaining is connected
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}