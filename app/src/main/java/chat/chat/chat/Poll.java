package chat.chat.chat;

public class Poll {
    String title,description;
    public Poll()
    {

    }
    public Poll(String title, String description, long timestamp, int forTheIssue, int againstTheIssue, int voted, int notVoted) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.forTheIssue = forTheIssue;
        this.againstTheIssue = againstTheIssue;
        this.voted = voted;
        this.notVoted = notVoted;
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

    public int getForTheIssue() {
        return forTheIssue;
    }

    public void setForTheIssue(int forTheIssue) {
        this.forTheIssue = forTheIssue;
    }

    public int getAgainstTheIssue() {
        return againstTheIssue;
    }

    public void setAgainstTheIssue(int againstTheIssue) {
        this.againstTheIssue = againstTheIssue;
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
    int forTheIssue,againstTheIssue,voted,notVoted;
}
