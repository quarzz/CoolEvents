package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.EventsDao;
import by.bsu.fpmi.dao.EventsDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/Login", "/Logout", "/Register"})
public class EventServlet extends HttpServlet{
    private final EventsDao eventsDao = new EventsDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String password = req.getParameter("password");
    }
}
