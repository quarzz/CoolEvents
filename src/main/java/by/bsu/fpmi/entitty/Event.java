package by.bsu.fpmi.entitty;

import java.util.Date;
import java.util.List;

public class Event {
    private String title;
    private String description;
    private Date date;
    private User owner;
    private List<User> sharedUsers;

    public Event() {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(List<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }
}
