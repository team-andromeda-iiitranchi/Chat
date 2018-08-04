package chat.chat.chat;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private String CR;
    private String Name;

    public Map getPolls() {
        return polls;
    }

    public void setPolls(Map polls) {
        this.polls = polls;
    }

    private Map polls;
    public String getIsUnseen() {
        return isUnseen;
    }

    public void setIsUnseen(String isUnseen) {
        this.isUnseen = isUnseen;
    }

    private String isUnseen;

    public long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    private long latestTimestamp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    private String username;
    public Users(String CR,String Name,String username,long latestTimestamp,String isUnseen,Map polls)
    {
        this.latestTimestamp=latestTimestamp;
        this.CR=CR;
        this.Name=Name;
        this.username=username;
        this.isUnseen=isUnseen;
        this.polls=polls;
    }

    public String getCR() {
        return CR;
    }

    public void setCR(String CR) {
        this.CR = CR;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public Users()
    {}
}
