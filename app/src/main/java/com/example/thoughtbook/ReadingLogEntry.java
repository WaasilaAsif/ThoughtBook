package com.example.thoughtbook;

public class ReadingLogEntry {
    String logId;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    String noteText;

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    String emotionName;
    String emotionColorHex;

    public String getEmotionColorHex() {
        return emotionColorHex;
    }

    public void setEmotionColorHex(String emotionColorHex) {
        this.emotionColorHex = emotionColorHex;
    }

    int pageAtLog;

    public int getPageAtLog() {
        return pageAtLog;
    }

    public void setPageAtLog(int pageAtLog) {
        this.pageAtLog = pageAtLog;
    }

    long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
