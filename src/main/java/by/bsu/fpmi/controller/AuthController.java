package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.AuthDao;
import by.bsu.fpmi.dao.AuthDaoImpl;
import by.bsu.fpmi.util.Constants;
import by.bsu.fpmi.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class AuthController extends HttpServlet {
    private AuthDao authDao;
    private Random random;

    public void init() {
        authDao = new AuthDaoImpl();
        random = new Random();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        switch (uri) {
            case "/login":
                req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                resp.sendRedirect("/");
                break;
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //handle potential exception
        int stage = Integer.parseInt(req.getParameter("stage"));

        if (stage == 1) {
            String login = req.getParameter("login");
            String password = req.getParameter("password");

            if (login != null && password != null) {
                int userID = authDao.authenticate(login, password);

                if (userID != Constants.NO_USER_ID) {
                    String token = generateToken();
                    authDao.addToken(userID, token, 1);

                    resp.addCookie(new Cookie("token", token));
                    resp.sendRedirect("/");
                    return;
                }
            }
        }

        resp.sendRedirect("/login");
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) {
        Cookie tokenCookie = Util.getCookieByName(req, "token");
        if (tokenCookie == null)
            return;

        String token = tokenCookie.getValue();
        authDao.deleteToken(token);
        tokenCookie.setMaxAge(0);
        resp.addCookie(tokenCookie);
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
