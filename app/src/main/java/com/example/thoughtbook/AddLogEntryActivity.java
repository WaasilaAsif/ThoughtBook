package com.example.thoughtbook;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AddLogEntryActivity extends AppCompatActivity {

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
        existingLogId = getIntent().getStringExtra("logId"); // will be null if not passed

        EditText pageInput = findViewById(R.id.pageInput);
        EditText noteInput = findViewById(R.id.noteInput);
        RadioGroup emotionGroup = findViewById(R.id.emotionGroup);

        // pre-fill fields if editing an existing entry
        if (existingLogId != null) {
            pageInput.setText(String.valueOf(getIntent().getIntExtra("pageAtLog", 0)));
            noteInput.setText(getIntent().getStringExtra("noteText"));
            String emotionName = getIntent().getStringExtra("emotionName");
            if (emotionName != null) {
                for (int i = 0; i < emotionGroup.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) emotionGroup.getChildAt(i);
                    if (rb.getText().toString().equals(emotionName)) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
            findViewById(R.id.deleteButton).setVisibility(android.view.View.VISIBLE);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            int page;
            try {
                page = Integer.parseInt(pageInput.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid page number", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = emotionGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Pick how it feels", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selected = findViewById(selectedId);

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