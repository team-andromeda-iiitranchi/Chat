package chat.chat.chat;

public class Users {
    private String CR;
    private String Name;

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
    public Users(String CR,String Name,String username,long latestTimestamp)
    {
        this.latestTimestamp=latestTimestamp;
        this.CR=CR;
        this.Name=Name;
        this.username=username;

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
