package chat.chat.chat;

import java.util.Map;

public class Users {
    private String CR,username,email;
    private String Name;
    private String isUnseen;
    private String imageLink;
    private Map<String,Object> polls;
    private long latestTimestamp;

    public Users(){}

    public Users(String CR, String email, String name, Map<String, Object> polls, String isUnseen, long latestTimestamp, String username,String imageLink) {
        this.CR = CR;
        Name = name;
        this.polls = polls;
        this.isUnseen = isUnseen;
        this.latestTimestamp = latestTimestamp;
        this.username = username;
        this.imageLink=imageLink;
    }


    public Users(String CR, String name, Map<String, Object> polls, String isUnseen, long latestTimestamp, String username,String imageLink) {
        this(CR,"",name,polls,isUnseen,latestTimestamp,username,imageLink);
    }

    public Users(String CR,String Name,String username,long latestTimestamp,String isUnseen,Map<String,Object> polls)
    {
        this(CR,Name,polls,isUnseen,latestTimestamp,username,"");
    }

    public String getCR() {
        return CR;
    }

    public void setCR(String CR) {
        this.CR = CR;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIsUnseen() {
        return isUnseen;
    }

    public void setIsUnseen(String isUnseen) {
        this.isUnseen = isUnseen;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Map<String, Object> getPolls() {
        return polls;
    }

    public void setPolls(Map<String, Object> polls) {
        this.polls = polls;
    }

    public long getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    @Override
    public String toString() {
        return "Users{" +
                "CR='" + CR + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", Name='" + Name + '\'' +
                ", isUnseen='" + isUnseen + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", polls=" + polls +
                ", latestTimestamp=" + latestTimestamp +
                '}';
    }
}
