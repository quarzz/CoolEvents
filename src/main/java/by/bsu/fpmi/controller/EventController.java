package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.*;
import by.bsu.fpmi.entity.Access;
import by.bsu.fpmi.entity.User;
import by.bsu.fpmi.util.Constants;
import by.bsu.fpmi.entity.Event;
import by.bsu.fpmi.util.DbUtil;
import by.bsu.fpmi.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {Constants.NEW_EVENT_URI, Constants.EVENT_URI})
public class EventController extends HttpServlet {

    private EventsDao eventsDao;
    private UserDao userDao;
    private SimpleDateFormat dateFormat;

    @Override
    public void init() throws ServletException {
        eventsDao = new EventsDaoImpl();
        userDao = new UserDaoImpl();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String action = req.getParameter("action");

        if (action == null) action = "show";

        switch (uri) {
            case Constants.NEW_EVENT_URI:
                setup(req, resp);
                return;
            case Constants.EVENT_URI:
                switch (action) {
                    case "show":
                        show(req, resp);
                        return;
                    case "edit":
                        edit(req, resp);
                        return;
                    case "delete":
                        delete(req, resp);
                        return;
                }
                return;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        Cookie tokenCookie = Util.getCookieByName(req, "token");
        String token = tokenCookie == null? "" : tokenCookie.getValue();

        int stage = new AuthDaoImpl().getStage(token);

        if (stage == -1) {
            resp.sendError(403);
            return;
        } else if (stage == 2) {
            Event event = eventFromReq(req);
            req.getSession().setAttribute("event", event);
            req.getSession().setAttribute("uri", uri);

            new AuthDaoImpl().setStage(token, 3);
            AuthController.generatePin(req);

            req.getRequestDispatcher("/approve.jsp").forward(req, resp);
        } else if (stage == 3) {
            int pin = Integer.parseInt(req.getParameter("pin"));

            int userId = new AuthDaoImpl().authenticate2(token, pin);

            if (userId == Constants.NO_USER_ID) {
                req.getRequestDispatcher("/approve.jsp").forward(req, resp);
                return;
            }


            try (
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement("delete from pins where token like ?");
            ) {
                statement.setString(1, token);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("littli shit))");
            }

            new AuthDaoImpl().setStage(token, 2);

            switch (uri) {
                case Constants.NEW_EVENT_URI:
                    create(req, resp);
                    return;
                case Constants.EVENT_URI:
                    update(req, resp);
                    return;
                default:
                    resp.sendError(404);
            }
        }
    }

    private void setup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Event event = new Event();

        List<User> allUsers = userDao.getUsers();
        allUsers.removeIf(user -> user.getId() == AccessController.getCurrentUserID(req));
        List<User> selectedUsers = new ArrayList<>();

        event.setSharedUsers(selectedUsers);

        for (User user : allUsers) {
            if (selectedUsers.contains(user))
                user.setSelected(true);
            else user.setSelected(false);
        }

        req.setAttribute("event", event);
        req.setAttribute("allUsers", allUsers);
        req.setAttribute("action", "new");
        req.getRequestDispatcher("new.jsp").forward(req, resp);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Event event = (Event) req.getSession().getAttribute("event");

        HttpSession session = req.getSession();
        String prevEventsListUrl = session.getAttribute("prevEventsListUrl") == null ? "/user" : session.getAttribute("prevEventsListUrl").toString();


        if (eventsDao.addEvent(event)) {
            resp.sendRedirect(prevEventsListUrl);
            System.out.println("CREATED");
        } else {
            resp.sendRedirect("/event/new");
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //here we will check access and parameters
        String rawEventId = req.getParameter("id");
        int eventId;
        int userId = AccessController.getCurrentUserID(req);

        if (rawEventId == null) {
            resp.sendError(404);
            return;
        }

        try {
            eventId = Integer.parseInt(rawEventId);
        } catch (NumberFormatException e) {
            resp.sendError(404);
            return;
        }

        int access = eventsDao.getAccess(userId, eventId);

        switch (access) {
            case Access.NONE:
                resp.sendError(403);
                return;
            case Access.READ:
                req.setAttribute("currentUserEvent", false);
                break;
            case Access.EDIT:
                req.setAttribute("currentUserEvent", true);
        }

        Event event = eventsDao.getEventById(eventId);

        req.setAttribute("event", event);
        req.setAttribute("access", access);
        req.getRequestDispatcher("show.jsp").forward(req, resp);
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        switch (eventsDao.getAccess(AccessController.getCurrentUserID(req), id)) {
            case Access.EDIT:
                break;
            case Access.READ:
            case Access.NONE:
                resp.sendError(403);
                return;
        }

        Event event = eventsDao.getEventById(id);

        List<User> allUsers = userDao.getUsers();
        allUsers.removeIf(user -> user.getId() == AccessController.getCurrentUserID(req));
        List<User> selectedUsers = new ArrayList<>();

        for (User user : allUsers) {
            int access = eventsDao.getAccess(user.getId(), event.getId());
            if (access == Access.READ)
                selectedUsers.add(user);
        }

        event.setSharedUsers(selectedUsers);

        for (User user : allUsers) {
            if (selectedUsers.contains(user))
                user.setSelected(true);
            else user.setSelected(false);
        }

        req.setAttribute("allUsers", allUsers);
        req.setAttribute("event", event);
        req.setAttribute("action", "edit");

        req.getRequestDispatcher("edit.jsp").forward(req, resp);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = AccessController.getCurrentUserID(req);

        Event event = (Event) req.getSession().getAttribute("event");

        HttpSession session = req.getSession();
        String prevEventsListUrl = session.getAttribute("prevEventsListUrl") == null ? "/user" : session.getAttribute("prevEventsListUrl").toString();

        if (eventsDao.updateEvent(event.getId(), event)) {
            resp.sendRedirect(prevEventsListUrl);
        } else {
            resp.sendRedirect(req.getRequestURI() + (req.getQueryString() == null ?'?' +req.getQueryString() : ""));
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        switch (eventsDao.getAccess(AccessController.getCurrentUserID(req), id)) {
            case Access.EDIT:
                break;
            case Access.READ:
            case Access.NONE:
                resp.sendError(403, "ONLY CREATOR HAVE ACCESS TO DELETE EVENT");
                return;
        }

        HttpSession session = req.getSession();
        String prevEventsListUrl =
                session.getAttribute("prevEventsListUrl") == null ? "/user" : session.getAttribute("prevEventsListUrl").toString();

        // some clean code goes here xD
        if (eventsDao.deleteEventById(id)) {
            resp.sendRedirect(prevEventsListUrl);
        } else {
            resp.sendRedirect(prevEventsListUrl);
        }
    }

    private Event eventFromReq(HttpServletRequest req) {
        Event event = new Event();

        String rawId = req.getParameter("id");

        if (rawId != null) {
            event.setId(Integer.parseInt(rawId));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        event.setTitle(req.getParameter("title"));
        event.setDescription(req.getParameter("description"));
        try {
            event.setDate(dateFormat.parse(req.getParameter("date")));
        } catch (ParseException e) {
            //HANDLE SOMEHOW IN FUTURE RELEASES
            e.printStackTrace();
        }

        event.setOwner(userDao.getUserById(AccessController.getCurrentUserID(req)));

        List<User> sharedUsers = new ArrayList<>();

        String[] allUsersIdsString = req.getParameterValues("allUsers");
        for (String userStringId : allUsersIdsString) {
            int id = Integer.parseInt(userStringId);
            User user = userDao.getUserById(id);
            sharedUsers.add(user);
        }

        event.setSharedUsers(sharedUsers);

        return event;
    }
}
