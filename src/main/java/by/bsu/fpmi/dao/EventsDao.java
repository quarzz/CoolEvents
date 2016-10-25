package by.bsu.fpmi.dao;

import by.bsu.fpmi.entitty.Event;

import java.util.List;

public interface EventsDao {

    public Event getEventById(int id);

    public boolean addEvent(Event event);

    public boolean deleteEventById(int id);

    public List<Event> getOwnersEvents(int ownerId);

    public List<Event> getSharedEvents(int userId);

    public int getAccess(int userId, int eventId);
}
