package by.bsu.fpmi.dao;

import by.bsu.fpmi.entity.Access;
import by.bsu.fpmi.entity.Event;
import by.bsu.fpmi.entity.User;
import by.bsu.fpmi.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventsDaoImpl implements EventsDao {

    protected Event createEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setDate(rs.getTimestamp("date"));
        return event;
    }

    public Event getEventById(int id) {
        Event event = null;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement(
                    "SELECT \"Events\".*, \"Users\".* FROM \"Events\"" +
                            "INNER JOIN \"Access\" ON \"Events\".id=\"Access\".event_id " +
                            "INNER JOIN \"Users\" ON \"Access\".user_id=\"Users\".id " +
                            "WHERE \"Events\".id=? AND \"Access\".access=? ORDER BY \"Events\".date")) {
                stat.setInt(1, id);
                stat.setInt(2, Access.EDIT);

                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        event = createEventFromResultSet(rs);
                        User user = new User();
                        user.setId(rs.getInt(5));
                        user.setFirstName(rs.getString("firstName"));
                        user.setLastName(rs.getString("lastName"));
                        user.setLogin(rs.getString("login"));
                        event.setOwner(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public boolean addEvent(Event event) {
        if (event.getOwner()==null)
            return false;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat1 = conn.prepareStatement(
                    "INSERT INTO \"Events\" (title, description, date) VALUES (?,?,?) RETURNING id");
                 PreparedStatement stat2 = conn.prepareStatement(
                         "INSERT INTO \"Access\" (user_id, event_id, access) VALUES (?,?,?)")) {

                conn.setAutoCommit(false);

                stat1.setString(1, event.getTitle());
                stat1.setString(2, event.getDescription());
                stat1.setTimestamp(3, new Timestamp(event.getDate().getTime()));

                try (ResultSet rs = stat1.executeQuery()) {
                    while (rs.next()) {
                        event.setId(rs.getInt("id"));
                    }
                }

                stat2.setInt(1, event.getOwner().getId());
                stat2.setInt(2, event.getId());
                stat2.setInt(3, Access.EDIT);
                stat2.addBatch();

                if (event.getSharedUsers()!=null) {
                    for (User user : event.getSharedUsers()) {
                        stat2.setInt(1, user.getId());
                        stat2.setInt(2, event.getId());
                        stat2.setInt(3, Access.READ);
                        stat2.addBatch();
                    }
                }

                stat2.executeBatch();

                conn.commit();
            } catch (SQLException e) {
                System.err.println("Transaction is being rolled back");
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean deleteEventById(int id) {
        boolean res = false;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat1 = conn.prepareStatement("DELETE FROM \"Access\" WHERE event_id=?");
                 PreparedStatement stat2 = conn.prepareStatement("DELETE FROM \"Events\" WHERE id=?")) {

                conn.setAutoCommit(false);

                stat1.setInt(1, id);
                int num = stat1.executeUpdate();
                stat2.setInt(1, id);
                num += stat2.executeUpdate();
                res = (num >= 2);

                conn.commit();
            } catch (SQLException e) {
                System.err.println("Transaction is being rolled back");
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean updateEvent(int id, Event event) {
        if (id!=event.getId())
            return false;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement updateStat = conn.prepareStatement(
                    "UPDATE \"Events\" SET title=?, description=?, date=? WHERE id=?");
                 PreparedStatement deleteStat = conn.prepareStatement(
                         "DELETE FROM \"Access\" WHERE event_id=? AND access=?");
                 PreparedStatement insertStat = conn.prepareStatement(
                         "INSERT INTO \"Access\" (user_id, event_id, access) VALUES (?,?,?)")) {

                conn.setAutoCommit(false);

                updateStat.setString(1, event.getTitle());
                updateStat.setString(2, event.getDescription());
                updateStat.setTimestamp(3, new Timestamp(event.getDate().getTime()));
                updateStat.setInt(4, event.getId());
                updateStat.executeUpdate();

                deleteStat.setInt(1, event.getId());
                deleteStat.setInt(2, Access.READ);
                deleteStat.executeUpdate();

                if (event.getSharedUsers()!=null) {
                    for (User user: event.getSharedUsers()) {
                        insertStat.setInt(1, user.getId());
                        insertStat.setInt(2, event.getId());
                        insertStat.setInt(3, Access.READ);
                        insertStat.addBatch();
                    }
                    insertStat.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                System.err.println("Transaction is being rolled back");
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Event> getOwnersEvents(int ownerId) {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement(
                    "SELECT \"Events\".* FROM \"Events\" INNER JOIN \"Access\" ON \"Events\".id=\"Access\".event_id " +
                            "WHERE \"Access\".access=? AND \"Access\".user_id=? ORDER BY \"Events\".date")) {
                stat.setInt(1, Access.EDIT);
                stat.setInt(2, ownerId);
                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        events.add(createEventFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public List<Event> getSharedEvents(int userId) {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("WITH shared_events AS (" +
                            "SELECT \"Events\".* FROM \"Events\" INNER JOIN \"Access\" ON \"Events\".id=\"Access\".event_id " +
                            "WHERE \"Access\".access=1 AND \"Access\".user_id=?)" +
                            "SELECT shared_events.*, \"Users\".* from shared_events " +
                            "INNER JOIN \"Access\" ON shared_events.id=\"Access\".event_id " +
                            "INNER JOIN \"Users\" ON \"Access\".user_id=\"Users\".id " +
                            "WHERE \"Access\".access=2 ORDER BY shared_events.date")) {
                stat.setInt(1, userId);

                try (ResultSet rs = stat.executeQuery()) {
                    Event event;
                    while (rs.next()) {
                        event = createEventFromResultSet(rs);
                        User user = new User();
                        user.setId(rs.getInt(5));
                        user.setFirstName(rs.getString("firstName"));
                        user.setLastName(rs.getString("lastName"));
                        user.setLogin(rs.getString("login"));

                        event.setOwner(user);
                        events.add(event);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public int getAccess(int userId, int eventId) {
        int access = Access.NONE;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT access FROM \"Access\" WHERE user_id=? AND event_id=?")) {
                stat.setInt(1, userId);
                stat.setInt(2, eventId);

                try (ResultSet rs = stat.executeQuery()) {
                    while (rs.next()) {
                        access = rs.getInt("access");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return access;
    }

    @Override
    public List<Event> getAllEvents(int user_id) {
        List<Event> events = new ArrayList<>();
        try (
            Connection conn = DbUtil.getConnection();
            PreparedStatement stat = conn.prepareStatement(
                "SELECT e.*, a.access FROM \"Events\" e JOIN \"Access\" a ON e.id = a.event_id WHERE a.user_id = ? AND a.access in (1, 2) ORDER BY e.date"
            )
        ) {
            stat.setInt(1, user_id);

            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setDate(rs.getTimestamp("date"));
                    event.setAccess(rs.getInt("access"));

                    event.setOwner(new UserDaoImpl().getUserById(getOwnerId(event.getId())));

                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public int getOwnerId(int eventId) {
        try (
            Connection conn = DbUtil.getConnection();
            PreparedStatement stat = conn.prepareStatement("SELECT a.user_id FROM \"Access\" a WHERE a.event_id = ? AND a.access = 2")
        ) {
            stat.setInt(1, eventId);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
