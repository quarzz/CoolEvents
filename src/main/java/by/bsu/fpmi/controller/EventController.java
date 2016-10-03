package by.bsu.fpmi.controller;

import by.bsu.fpmi.Constants;
import by.bsu.fpmi.dao.EventsDaoImpl;
import by.bsu.fpmi.entitty.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(urlPatterns = {Constants.NEW_EVENT_URI, Constants.SHOW_EVENT_URI})
public class EventController extends HttpServlet {

    private EventsDaoImpl dao;

    @Override
    public void init() throws ServletException {
        dao = new EventsDaoImpl();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        switch (uri) {
            case Constants.NEW_EVENT_URI:
                setup(req, resp);
                break;
            case Constants.SHOW_EVENT_URI:
                show(req, resp);
                break;
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
                break;
            default:
                resp.sendError(404);
        }
    }

    private void setup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        }

        if (dao.addEvent(event)) {
            resp.sendRedirect("/event/");
        } else {
            resp.sendRedirect("/event/new");
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //here we will check access and parameters
        String rawId = req.getParameter("id");
        int id;

        if (rawId == null) {

            return;
        }

        try {
            id = Integer.parseInt(rawId);
        } catch (NumberFormatException e) {
            resp.sendError(404);
            return;
        }

        Event event = new Event();
        event.setTitle("Влад сварил борщ");
        event.setDescription("Влад сварил самый вкусный борщ, который ты когда-либо пробовал в своей жизни.\nА чего добился ты??");
        event.setDate(new Date());

        req.setAttribute("event", event);
        req.getRequestDispatcher("show.jsp").forward(req, resp);
    }
}
