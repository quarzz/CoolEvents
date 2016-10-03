package by.bsu.fpmi.controller;

import by.bsu.fpmi.Constants;
import by.bsu.fpmi.dao.UserDaoImpl;
import by.bsu.fpmi.entitty.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {Constants.NEW_USER_URI, Constants.SHOW_USER_URI})
public class UserController extends HttpServlet {

    private UserDaoImpl dao;

    @Override
    public void init() throws ServletException {
        dao = new UserDaoImpl();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri  = req.getRequestURI();

        switch (uri) {
            case Constants.NEW_USER_URI:
                setup(req, resp);
                break;
            case Constants.SHOW_USER_URI:
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
            case Constants.NEW_USER_URI:
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
        User user = new User();
        user.setFirstName(req.getParameter("first_name"));
        user.setLastName(req.getParameter("last_name"));
        user.setLogin(req.getParameter("email"));
        user.setLogin(req.getParameter("password"));

        if (dao.addUser(user)) {
            resp.sendRedirect("/user/?id=" + user.getId());
        } else {
            resp.sendRedirect("user/new");
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String rawId = req.getParameter("id");
        int id;

        if (rawId == null) {
            resp.sendError(404, "No ID");
            return;
        }

        try {
            id = Integer.parseInt(rawId);
        } catch (NumberFormatException e) {
            resp.sendError(404, "Provided ID is a peace of SHIT");
            return;
        }

        //here in future we need to get user from dao by ID from GET parameters
        User user = new User();
        user.setId(1);
        user.setFirstName("Vlad");
        user.setLastName("Matulis");
        user.setLogin("quarzzap@gmail.com");
        user.setPassword("xyu");
        ///////////////////////////////////////////////////////////////////////

        req.setAttribute("user", user);
        req.getRequestDispatcher("show.jsp").forward(req, resp);
    }
}
