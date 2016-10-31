package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.EventsDao;
import by.bsu.fpmi.dao.EventsDaoImpl;
import by.bsu.fpmi.dao.UserDao;
import by.bsu.fpmi.entity.Event;
import by.bsu.fpmi.util.Constants;
import by.bsu.fpmi.dao.UserDaoImpl;
import by.bsu.fpmi.entity.User;
import by.bsu.fpmi.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {Constants.NEW_USER_URI, Constants.USER_URI})
public class UserController extends HttpServlet {

    private UserDao userDao;
    private EventsDao eventsDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
        eventsDao = new EventsDaoImpl();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri  = req.getRequestURI();

        switch (uri) {
            case Constants.NEW_USER_URI:
                setup(req, resp);
                break;
            case Constants.USER_URI:
                show(req, resp);
                break;
            default:
                resp.sendError(404);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();

        switch (uri) {
            case Constants.NEW_USER_URI:
                create(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void setup(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("new.jsp").forward(req, resp);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = new User();
        user.setFirstName(req.getParameter("first_name"));
        user.setLastName(req.getParameter("last_name"));
        user.setLogin(req.getParameter("email"));
        user.setPassword(req.getParameter("password"));

        if (userDao.addUser(user)) {
            resp.sendRedirect("/user/?id=" + user.getId());
        } else {
            req.setAttribute("alerts", new String[]{"Invalid data provided - please, try one more time."});
            req.getRequestDispatcher("new.jsp").forward(req, resp);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String rawId = req.getParameter("id");
        int userId;
        int currentUserId = AccessController.getCurrentUserID(req);

        if (rawId == null) {
            userId = currentUserId;
        } else {
            try {
                userId = Integer.parseInt(rawId);
            } catch (NumberFormatException e) {
                resp.sendError(404, "Provided ID is a peace of SHIT");
                return;
            }
        }

        User user = userDao.getUserById(userId);
        if (user == null) {
            resp.sendError(404);
            return;
        }

        if (userId == currentUserId) {
            user.setMyEvents(eventsDao.getOwnersEvents(userId).stream().map(event -> {
                event.setOwner(user);
                return event;
            }).collect(Collectors.toList()));
            user.setReadingEvents(eventsDao.getSharedEvents(userId));
        } else {
            List<Event> sharedWithCurrentUser = eventsDao.getSharedEvents(userId);

            List<Event> sharedFromUser;
            sharedFromUser = sharedWithCurrentUser.stream().
                            filter(event -> event.getOwner().getId() == userId).
                            collect(Collectors.toList());

            user.setReadingEvents(sharedFromUser);
        }

        req.getSession().setAttribute("prevEventsListUrl", Util.getFullURL(req));

        req.setAttribute("user", user);
        req.getRequestDispatcher("show.jsp").forward(req, resp);
    }
}
