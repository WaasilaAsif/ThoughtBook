package com.example.thoughtbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import android.widget.ProgressBar;

public class BookDetailActivity extends AppCompatActivity {

    private BookRepository repository;
    private String bookId;
    private Book currentBook;
    private LogTimelineAdapter timelineAdapter;
    private String buildStars(float rating) {
        int filled = Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < filled ? "★" : "☆");
        }
        return sb.toString();
    }

    private void setupStarRating(LinearLayout container, Book book) {
        container.removeAllViews();
        int currentRating = Math.round(book.getPersonalRating());

        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            TextView star = new TextView(this);
            star.setText(i <= currentRating ? "★" : "☆");
            star.setTextSize(24);
            star.setTextColor(getResources().getColor(R.color.accent));
            star.setPadding(4, 0, 4, 0);
            star.setOnClickListener(v -> {
                repository.updateRating(currentBook.getBookId(), starValue);
            });
            container.addView(star);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookId = getIntent().getStringExtra("bookId");
        String uid = FirebaseAuth.getInstance().getUid();
        repository = new BookRepository(uid);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.addLogButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLogEntryActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });
        RecyclerView timelineList = findViewById(R.id.logTimelineList);
        timelineList.setLayoutManager(new LinearLayoutManager(this));
        //timelineAdapter = new LogTimelineAdapter(new ArrayList<>());

        timelineAdapter = new LogTimelineAdapter(new ArrayList<>(), entry -> {
            Intent intent = new Intent(this, AddLogEntryActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("logId", entry.getLogId());
            intent.putExtra("pageAtLog", entry.getPageAtLog());
            intent.putExtra("noteText", entry.getNoteText());
            intent.putExtra("emotionName", entry.getEmotionName());
            startActivity(intent);
        });

        timelineList.setAdapter(timelineAdapter);

        TextView titleText = findViewById(R.id.detailTitle);
        TextView authorText = findViewById(R.id.detailAuthor);
        TextView paceText = findViewById(R.id.detailPace);

        repository.getBook(bookId).observe(this, book -> {
            if (book == null) return;
            currentBook = book;
            titleText.setText(book.getTitle());
            authorText.setText(book.getAuthors() != null ? String.join(", ", book.getAuthors()) : "");

            ImageView coverImage = findViewById(R.id.detailCoverImage);
            TextView fallbackTitle = findViewById(R.id.detailFallbackTitle);
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                coverImage.setVisibility(View.VISIBLE);
                fallbackTitle.setVisibility(View.GONE);
                Glide.with(this).load(book.getCoverUrl()).into(coverImage);
            } else {
                coverImage.setVisibility(View.GONE);
                fallbackTitle.setVisibility(View.VISIBLE);
                fallbackTitle.setText(book.getTitle());
                if (book.getCurrentEmotionColorHex() != null) {
                    fallbackTitle.setBackgroundTintList(null);
                    fallbackTitle.getBackground().setTint(Color.parseColor(book.getCurrentEmotionColorHex()));
                }
            }

            ImageView emotionDot = findViewById(R.id.emotionDot);
            TextView emotionLabel = findViewById(R.id.emotionLabel);
            if (book.getCurrentEmotionColorHex() != null) {
                emotionDot.setColorFilter(Color.parseColor(book.getCurrentEmotionColorHex()));
            }

            LinearLayout starContainer = findViewById(R.id.starRatingContainer);
            setupStarRating(starContainer, book);

            updatePaceDisplay(findViewById(R.id.pagesProgressText),
                    findViewById(R.id.paceProgressBar),
                    findViewById(R.id.detailPace));
        });

//        repository.getLogTimeline(bookId).observe(this, entries -> {
//            timelineAdapter.updateEntries(entries);
//            // recalculate pace whenever new log entries come in
//            updatePaceDisplay(paceText);
//        });
    }

//    private void updatePaceDisplay(TextView pagesText, ProgressBar bar, TextView paceText) {
//        repository.getLogTimeline(bookId).observe(this, entries -> {
//            if (currentBook == null) return;
//            double minutesLeft = repository.estimateMinutesRemaining(currentBook, entries);
//            int hours = (int) (minutesLeft / 60);
//            int mins = (int) (minutesLeft % 60);
//            pagesText.setText("page " + currentBook.getCurrentPage() + " / " + currentBook.getTotalPages());
//            int percent = currentBook.getTotalPages() > 0
//                    ? (int) (100.0 * currentBook.getCurrentPage() / currentBook.getTotalPages()) : 0;
//            bar.setProgress(percent);
//            paceText.setText("~" + hours + "h " + mins + "m left at your pace");
//
//            // update the emotion label from the latest log entry
//            TextView emotionLabel = findViewById(R.id.emotionLabel);
//            if (!entries.isEmpty()) {
//                emotionLabel.setText(entries.get(entries.size() - 1).getEmotionName());
//            }
//        });
private void updatePaceDisplay(TextView pagesText, ProgressBar bar, TextView paceText) {
    repository.getLogTimeline(bookId).observe(this, entries -> {
        timelineAdapter.updateEntries(entries);

        if (currentBook == null) return;
        double minutesLeft = repository.estimateMinutesRemaining(currentBook, entries);
        int hours = (int) (minutesLeft / 60);
        int mins = (int) (minutesLeft % 60);
        pagesText.setText("page " + currentBook.getCurrentPage() + " / " + currentBook.getTotalPages());
        int percent = currentBook.getTotalPages() > 0
                ? (int) (100.0 * currentBook.getCurrentPage() / currentBook.getTotalPages()) : 0;
        bar.setProgress(percent);
        paceText.setText("~" + hours + "h " + mins + "m left at your pace");

        TextView emotionLabel = findViewById(R.id.emotionLabel);
        if (!entries.isEmpty()) {
            emotionLabel.setText(entries.get(entries.size() - 1).getEmotionName());
        }
    });
}
}