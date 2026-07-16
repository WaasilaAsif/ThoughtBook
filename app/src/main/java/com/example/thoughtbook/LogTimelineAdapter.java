package com.example.thoughtbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LogTimelineAdapter extends RecyclerView.Adapter<LogTimelineAdapter.ViewHolder> {
    private List<ReadingLogEntry> entries;
    private final OnLogClickListener listener;
    public LogTimelineAdapter(List<ReadingLogEntry> entries, OnLogClickListener listener) {
        this.entries = entries;
        this.listener = listener;
    }
    public interface OnLogClickListener {
        void onLogClick(ReadingLogEntry entry);
    }
    public void updateEntries(List<ReadingLogEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView meta, note;
        public ViewHolder(View view) {
            super(view);
            meta = view.findViewById(R.id.logMeta);
            note = view.findViewById(R.id.logNote);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadingLogEntry entry = entries.get(position);
        String dateStr = new SimpleDateFormat("MMM d", Locale.getDefault()).format(entry.timestamp);
        holder.meta.setText(entry.emotionName + " · page " + entry.pageAtLog + " · " + dateStr);
        holder.note.setText(entry.noteText);
        holder.itemView.setOnClickListener(v -> listener.onLogClick(entry));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}