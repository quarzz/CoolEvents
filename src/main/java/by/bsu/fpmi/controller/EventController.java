package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.EventsDao;
import by.bsu.fpmi.dao.UserDao;
import by.bsu.fpmi.dao.UserDaoImpl;
import by.bsu.fpmi.entity.Access;
import by.bsu.fpmi.entity.User;
import by.bsu.fpmi.util.Constants;
import by.bsu.fpmi.dao.EventsDaoImpl;
import by.bsu.fpmi.entity.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
        Event event = new Event();

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

        int id = Integer.parseInt(req.getParameter("id"));

        Event event = eventsDao.getEventById(id);

        event.setTitle(req.getParameter("title"));
        event.setDescription(req.getParameter("description"));
        try {
            event.setDate(dateFormat.parse(req.getParameter("date")));
        } catch (ParseException e) {
            event.setDate(null);
        }

        List<User> sharedUsers = new ArrayList<>();

        String[] allUsersIdsString = req.getParameterValues("allUsers");
        for (String userStringId : allUsersIdsString) {
            int currentUserId = Integer.parseInt(userStringId);
            User user = userDao.getUserById(currentUserId);
            sharedUsers.add(user);
        }

        event.setSharedUsers(sharedUsers);

        HttpSession session = req.getSession();
        String prevEventsListUrl = session.getAttribute("prevEventsListUrl") == null ? "/user" : session.getAttribute("prevEventsListUrl").toString();

        if (eventsDao.updateEvent(id, event)) {
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
}
