package chat.chat.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poll {
    String title,description;
    public Poll(String titleStr, String descriptionStr, long timestamp, int i, int count, List list)
    {
        this.title = titleStr;
        this.description = descriptionStr;
        this.timestamp = timestamp;
        this.voted = i;
        this.notVoted = count;
        optionsMap=new HashMap<>();
        for(int a=0;a<list.size();a++)
        {
            optionsMap.put((String) list.get(a),0);
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVoted() {
        return voted;
    }

    public void setVoted(int voted) {
        this.voted = voted;
    }

    public int getNotVoted() {
        return notVoted;
    }

    public void setNotVoted(int notVoted) {
        this.notVoted = notVoted;
    }

    long timestamp;
    int voted;
    int notVoted;

    public Map<String, Integer> getOptionsMap() {
        return optionsMap;
    }

    public void setOptionsMap(Map<String, Integer> optionsMap) {
        this.optionsMap = optionsMap;
    }

    Map<String,Integer> optionsMap;
    public Poll()
    {

    }
}
