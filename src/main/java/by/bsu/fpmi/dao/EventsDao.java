package by.bsu.fpmi.dao;

import by.bsu.fpmi.entity.Event;

import java.util.List;

public interface EventsDao {

    public Event getEventById(int id);

    public boolean addEvent(Event event);

    public boolean deleteEventById(int id);

    public boolean updateEvent(int id, Event event);

    public List<Event> getOwnersEvents(int ownerId);

    public List<Event> getSharedEvents(int userId);

    public int getAccess(int userId, int eventId);

    public List<Event> getAllEvents(int user_id);

    public int getOwnerId(int eventId);
}
