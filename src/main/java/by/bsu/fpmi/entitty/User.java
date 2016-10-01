package by.bsu.fpmi.entitty;

import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private List<Event> myEvents;
    private List<Event> readingEvents;

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Event> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(List<Event> myEvents) {
        this.myEvents = myEvents;
    }

    public List<Event> getReadingEvents() {
        return readingEvents;
    }

    public void setReadingEvents(List<Event> readingEvents) {
        this.readingEvents = readingEvents;
    }
}
