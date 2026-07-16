package com.example.thoughtbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class LogBookActivity extends AppCompatActivity {

    private BookRepository repository;
    private List<CheckBox> shelfCheckboxes = new ArrayList<>();
    private List<Shelf> loadedShelves = new ArrayList<>();

    private String bookTitle, bookAuthor, coverUrl, googleBooksId, bookGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book);
        bookGenre = getIntent().getStringExtra("genre");
        String uid = FirebaseAuth.getInstance().getUid();
        repository = new BookRepository(uid);

        bookTitle = getIntent().getStringExtra("title");
        bookAuthor = getIntent().getStringExtra("author");
        coverUrl = getIntent().getStringExtra("coverUrl");
        googleBooksId = getIntent().getStringExtra("googleBooksId");

        TextView titleText = findViewById(R.id.bookTitleText);
        TextView authorText = findViewById(R.id.bookAuthorText);
        titleText.setText(bookTitle);
        authorText.setText(bookAuthor);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        loadShelvesIntoCheckboxes();

        findViewById(R.id.saveButton).setOnClickListener(v -> saveBookAndLog());
    }

    private void loadShelvesIntoCheckboxes() {
        android.widget.LinearLayout container = findViewById(R.id.shelfCheckboxContainer);
        repository.getShelves().observe(this, shelves -> {
            container.removeAllViews();
            shelfCheckboxes.clear();
            loadedShelves = shelves;
            for (Shelf shelf : shelves) {
                CheckBox cb = new CheckBox(this);
                cb.setText(shelf.getName());
                cb.setTag(shelf.getShelfId());
                container.addView(cb);
                shelfCheckboxes.add(cb);
            }
        });
    }

    private void saveBookAndLog() {
        EditText totalPagesInput = findViewById(R.id.totalPagesInput);
        EditText noteInput = findViewById(R.id.noteInput);
        RadioGroup emotionGroup = findViewById(R.id.emotionGroup);
        //book.setAuthors(java.util.Arrays.asList(bookAuthor.split(", ")));
        int totalPages;
        try {
            totalPages = Integer.parseInt(totalPagesInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid page count", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedEmotionId = emotionGroup.getCheckedRadioButtonId();
        if (selectedEmotionId == -1) {
            Toast.makeText(this, "Pick how it feels", Toast.LENGTH_SHORT).show();
            return;
        }
        android.widget.RadioButton selectedButton = findViewById(selectedEmotionId);
        String emotionName = selectedButton.getText().toString();
        String emotionColorHex = selectedButton.getTag().toString();

        List<String> selectedShelfIds = new ArrayList<>();
        for (CheckBox cb : shelfCheckboxes) {
            if (cb.isChecked()) selectedShelfIds.add(cb.getTag().toString());
        }

        String bookId = java.util.UUID.randomUUID().toString();


        //Started editing here if it fails likely something went wrong here

        Book book = new Book();
        book.setBookId(bookId);
        book.setGoogleBooksId(googleBooksId);
        book.setTitle(bookTitle);
        book.setAuthors(java.util.Arrays.asList(bookAuthor.split(", ")));
        book.setCoverUrl(coverUrl);
        book.setStatus(ShelfStatus.READING);
        book.setShelfIds(selectedShelfIds);
        book.setTotalPages(totalPages);
        book.setCurrentPage(0);
        book.setGenre(bookGenre);
        book.setCurrentEmotionColorHex(emotionColorHex);
        book.setDateAdded(System.currentTimeMillis());


        repository.logBook(book);

        ReadingLogEntry entry = new ReadingLogEntry();
        entry.logId = java.util.UUID.randomUUID().toString();
        entry.noteText = noteInput.getText().toString().trim();
        entry.emotionName = emotionName;
        entry.emotionColorHex = emotionColorHex;
        entry.pageAtLog = 0;
        entry.timestamp = System.currentTimeMillis();

        repository.addLogEntry(bookId, entry);

        Toast.makeText(this, "Book logged!", Toast.LENGTH_SHORT).show();
        finish();
    }
}