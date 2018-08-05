package chat.chat.chat;

import java.util.Map;

public class Users {
    private String CR;
    private String Name;

    public Users(String CR, String name, Map<String, Object> polls, String isUnseen, long latestTimestamp, String username) {
        this.CR = CR;
        Name = name;
        this.polls = polls;
        this.isUnseen = isUnseen;
        this.latestTimestamp = latestTimestamp;
        this.username = username;
    }

    public Map<String,Object> getPolls() {
        return polls;
    }

    public void setPolls(Map<String,Object> polls) {
        this.polls =polls;
    }

    private Map<String,Object> polls;
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
    public Users(String CR,String Name,String username,long latestTimestamp,String isUnseen,Map<String,Object> polls)
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
