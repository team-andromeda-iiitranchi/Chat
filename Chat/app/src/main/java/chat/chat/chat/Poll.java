package chat.chat.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Poll {
    String title,description;
    public Poll(String titleStr, String descriptionStr, long timestamp, int count, List list)
    {
        this.title = titleStr;
        this.description = descriptionStr;
        this.timestamp = timestamp;
        this.total = count;
        optionsMap=new HashMap<>();
        for(int a=0;a<list.size();a++)
        {
            optionsMap.put((String) list.get(a),0l);
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    long timestamp;
    int total;

    public Map<String, Long> getOptionsMap() {
        return optionsMap;
    }

    public void setOptionsMap(Map<String, Long> optionsMap) {
        this.optionsMap = optionsMap;
    }

    Map<String,Long> optionsMap;
    public Poll()
    {

    }
}
