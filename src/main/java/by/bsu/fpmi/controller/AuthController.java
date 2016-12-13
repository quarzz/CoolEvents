package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.AuthDao;
import by.bsu.fpmi.dao.AuthDaoImpl;
import by.bsu.fpmi.dao.UserDao;
import by.bsu.fpmi.dao.UserDaoImpl;
import by.bsu.fpmi.entity.User;
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
    private UserDao userDao;
    private Random random;

    public void init() {
        authDao = new AuthDaoImpl();
        userDao = new UserDaoImpl();
        random = new Random();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        switch (uri) {
            case "/login":
                login(req, resp);
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
        int stage;
        try {
            stage = Integer.parseInt(req.getParameter("stage"));
        } catch (NumberFormatException e) {
            resp.sendRedirect("/login");
            return;
        }

        if (stage == 1) {
            String login = req.getParameter("login");
            String password = req.getParameter("password");

            if (login != null && password != null) {
                int userID = authDao.authenticate(login, password);

                if (userID != Constants.NO_USER_ID) {
                    String token = generateToken();
                    authDao.addToken(userID, token, 1);

                    resp.addCookie(new Cookie("token", token));
                    resp.sendRedirect("/login");
                    return;
                }
            }
        } else if (stage == 2) {
            int pin_submitted = Integer.parseInt(req.getParameter("pin"));
            Cookie tokenCookie = Util.getCookieByName(req, "token");
            int user_id = authDao.authenticate2(tokenCookie.getValue(), pin_submitted);
            if ( user_id != Constants.NO_USER_ID) {
                authDao.deleteToken(tokenCookie.getValue());

                String token = generateToken();

                resp.addCookie(new Cookie("token", token));
                authDao.addToken(user_id, token, 2);
                resp.sendRedirect("/");
                return;
            } else {
                resp.sendRedirect("/login");
                return;
            }
        }

        resp.sendError(400);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        int stage = Util.getAuthStage(req);

        if (stage == 1) {
            req.getRequestDispatcher("/auth/login1.jsp").forward(req, resp);
        } else if (stage == 2) {
            generatePin(req);
            req.getRequestDispatcher("/auth/login2.jsp").forward(req, resp);
        }
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

    //here should be token cookie in req
    private void generatePin(HttpServletRequest req) {
        Cookie tokenCookie = Util.getCookieByName(req, "token");
        if (tokenCookie == null || authDao.getPin(tokenCookie.getValue()) != -1)
            return;

        int pin = new Random().nextInt(999999 - 100000 + 1) + 100000;
        authDao.setPin(tokenCookie.getValue(), pin);

        int user_id = userDao.getUserIdByToken(tokenCookie.getValue(), 1);
        User user = userDao.getUserById(user_id);

        Util.sendMail(user.getLogin(), String.valueOf(pin));
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
