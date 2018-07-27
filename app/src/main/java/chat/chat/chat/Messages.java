package chat.chat.chat;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
        if(isPresent==-1||isPresent==str.length()-1) {
            list.add("An");
            return list;
        }
        while(isPresent!=-1) {
            String start = str.substring(isPresent + 1);
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
        if(list==null)
        {
            list.add("An");
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