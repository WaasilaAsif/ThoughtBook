package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {

    private BookRepository repository;
    private String bookId;
    private Book currentBook;
    private LogTimelineAdapter timelineAdapter;

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
            updatePaceDisplay(paceText);
        });

        repository.getLogTimeline(bookId).observe(this, entries -> {
            timelineAdapter.updateEntries(entries);
            // recalculate pace whenever new log entries come in
            updatePaceDisplay(paceText);
        });
    }

    private void updatePaceDisplay(TextView paceText) {
        if (currentBook == null) return;
        repository.getLogTimeline(bookId).observe(this, entries -> {
            double minutesLeft = repository.estimateMinutesRemaining(currentBook, entries);
            int hours = (int) (minutesLeft / 60);
            int mins = (int) (minutesLeft % 60);
            paceText.setText("page " + currentBook.getCurrentPage() + " / " + currentBook.getTotalPages()
                    + " — ~" + hours + "h " + mins + "m left at your pace");
        });
    }
}