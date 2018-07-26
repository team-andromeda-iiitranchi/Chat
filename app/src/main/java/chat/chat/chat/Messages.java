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
    public boolean isEqual(Messages messages)
    {
        boolean res=false;
        if(messages.getTimestamp()==this.timestamp)
        {
            if(messages.getText().equals(this.text))
            {
                if(messages.getFrom()==this.from)
                {
                    res=true;
                }
            }

        }
        return res;
    }
    public String getHashTag()
    {
        int isPresent=text.indexOf("#");
        if(isPresent==-1||isPresent==text.length()-1)
        {
            return "An";
        }
        else
        {
            String start=text.substring(isPresent+1);
            int isSpace=start.indexOf(" ");
            String full;
            if(isSpace==-1) {
                 full = start;
            }
            else
            {
                full=start.substring(0,isSpace);
            }
            full=full.toUpperCase();
            return full;
        }
    }
} 