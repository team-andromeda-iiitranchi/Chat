package chat.chat.chat;

import android.os.Message;

public class Messages {
    Messages()
    {}
    public String getFrom() {
        return from;
    }

    public String getSeen() {
        return seen;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Messages(String from, String seen, String text, long timestamp) {
        this.from = from;
        this.seen = seen;
        this.text = text;
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    String from,seen,text;
    long timestamp;

} 