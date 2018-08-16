package chat.chat.chat;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    Messages()
    {}
    public String getFrom() {
        return from;
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

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    String from;
    String text;

    public Messages(String from, String text, String link, String type, long timestamp) {
        this.from = from;
        this.text = text;
        this.link = link;
        this.type = type;
        this.timestamp = timestamp;
    }

    String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;
    long timestamp;
    public boolean isEqual(Messages messages)
    {
        boolean res=false;
        if(messages.getTimestamp()==this.timestamp)
        {
            if(messages.getText().equals(this.text))
            {
                if(messages.getFrom().equals(this.from))
                {
                    res=true;
                }
            }

        }
        return res;
    }
    public List getHashTag()
    {
        text=text.trim();
        int isPresent=text.indexOf("#");
        String str=text;
        List<String> list=new ArrayList<>();
        list.add("An");
        if(isPresent==-1||isPresent==str.length()-1) {
            return list;
        }
        while(isPresent!=-1) {
            String start = str.substring(isPresent + 1);
            str=start;
            int isSpace = start.indexOf(" ");
            String full;
            if (isSpace == -1) {
                full = start;
            } else {
                full = start.substring(0, isSpace);
            }
            full = full.toUpperCase();
            //add in list
            if(isValid(full)) {
                list.add(full);
            }
            else
            {
                break;
            }
            if(isSpace!=-1) {
                str = str.substring(isSpace + 1);
            }
            else
            {
                break;
            }
            isPresent=str.indexOf("#");

        }
        return list;
    }
    public boolean isValid(String full)
    {
        for(int i=0;i<full.length();i++)
        {
            char c=full.charAt(i);
            if(!(Character.isDigit(c)||Character.isLetter(c)))
            {
               return false;
            }
        }
        return true;
    }
    public int compareTo(Messages messages)
    {
        if(messages.getTimestamp()>timestamp)
        {
            return 1;
        }
        else if(messages.getTimestamp()<timestamp)
        {
            return -1;
        }
        return 0;
    }
} 