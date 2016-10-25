package by.bsu.fpmi.dao;

import by.bsu.fpmi.entitty.Access;
import by.bsu.fpmi.entitty.Event;
import by.bsu.fpmi.entitty.User;
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
        boolean res = false;
        if (event.getOwner()==null) return false;
        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement stat1 = conn.prepareStatement(
                    "INSERT INTO \"Events\" (title, description, date) VALUES (?,?,?) RETURNING id");
                 PreparedStatement stat2 = conn.prepareStatement(
                         "INSERT INTO \"Access\" (event_id, user_id, access) VALUES (?,?,?)")) {

                conn.setAutoCommit(false);

                stat1.setString(1, event.getTitle());
                stat1.setString(2, event.getDescription());
                stat1.setTimestamp(3, new Timestamp(event.getDate().getTime()));

                try (ResultSet rs = stat1.executeQuery()) {
                    while (rs.next()) {
                        event.setId(rs.getInt("id"));
                    }
                }
                System.out.println("");

                stat2.setInt(1, event.getId());
                stat2.setInt(2, event.getOwner().getId());
                stat2.setInt(3, Access.EDIT);
                int num = stat2.executeUpdate();

                res = (num>0);
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
}
