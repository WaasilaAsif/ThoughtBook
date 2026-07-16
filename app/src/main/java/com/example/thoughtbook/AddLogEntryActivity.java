package com.example.thoughtbook;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AddLogEntryActivity extends AppCompatActivity {
    private int currentPageValue = 0;
    private BookRepository repository;
    private String bookId;
    private String existingLogId; // null = create mode, non-null = edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log_entry);

        String uid = FirebaseAuth.getInstance().getUid();
        repository = new BookRepository(uid);

        bookId = getIntent().getStringExtra("bookId");
        existingLogId = getIntent().getStringExtra("logId"); // null if not passed

        TextView pageInput = findViewById(R.id.pageInput);
        TextView pageOfText = findViewById(R.id.pageOfText);
        int totalPages = getIntent().getIntExtra("totalPages", 0);
        pageOfText.setText("of " + totalPages + " pages");

        if (existingLogId != null) {
            currentPageValue = getIntent().getIntExtra("pageAtLog", 0);
        }
        pageInput.setText(String.valueOf(currentPageValue));

        findViewById(R.id.pageMinusButton).setOnClickListener(v -> {
            if (currentPageValue > 0) currentPageValue--;
            pageInput.setText(String.valueOf(currentPageValue));
        });

        findViewById(R.id.pagePlusButton).setOnClickListener(v -> {
            if (totalPages == 0 || currentPageValue < totalPages) currentPageValue++;
            pageInput.setText(String.valueOf(currentPageValue));
        });

        EditText noteInput = findViewById(R.id.noteInput);
        ChipGroup emotionGroup = findViewById(R.id.emotionGroup);
        // pre-fill note + emotion if editing (page is already handled above)
        if (existingLogId != null) {
            noteInput.setText(getIntent().getStringExtra("noteText"));
            String emotionName = getIntent().getStringExtra("emotionName");
            if (emotionName != null) {
                for (int i = 0; i < emotionGroup.getChildCount(); i++) {
                    android.view.View child = emotionGroup.getChildAt(i);
                    if (child instanceof Chip) {
                        Chip chip = (Chip) child;
                        if (chip.getText().toString().equals(emotionName)) {
                            chip.setChecked(true);
                            break;
                        }
                    }
                }
            }
            findViewById(R.id.deleteButton).setVisibility(android.view.View.VISIBLE);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            int page = currentPageValue;

            int selectedId = emotionGroup.getCheckedChipId();
            if (selectedId == android.view.View.NO_ID) {
                Toast.makeText(this, "Pick how it feels", Toast.LENGTH_SHORT).show();
                return;
            }
            Chip selected = findViewById(selectedId);
            ReadingLogEntry entry = new ReadingLogEntry();
            entry.setLogId(existingLogId != null ? existingLogId : java.util.UUID.randomUUID().toString());
            entry.setPageAtLog(page);
            entry.setEmotionName(selected.getText().toString());
            entry.setEmotionColorHex(selected.getTag().toString());
            entry.setNoteText(noteInput.getText().toString().trim());
            entry.setTimestamp(System.currentTimeMillis());

            if (existingLogId != null) {
                repository.updateLogEntry(bookId, existingLogId, entry);
                Toast.makeText(this, "Log entry updated", Toast.LENGTH_SHORT).show();
            } else {
                repository.addLogEntry(bookId, entry);
                Toast.makeText(this, "Log entry added", Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        findViewById(R.id.deleteButton).setOnClickListener(v -> {
            if (existingLogId != null) {
                repository.deleteLogEntry(bookId, existingLogId);
                Toast.makeText(this, "Log entry deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}