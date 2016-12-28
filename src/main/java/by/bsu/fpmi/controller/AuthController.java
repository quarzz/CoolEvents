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

@WebServlet(urlPatterns = {"/login", "/logout", "/resend"})
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
            case "/resend":
                resendPin(req, resp);
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
            String remember = req.getParameter("remember");

            System.out.println("REMEMBER: " + (remember != null));

            if (login != null && password != null) {
                int userID = authDao.authenticate(login, password);

                if (userID != Constants.NO_USER_ID) {
                    String token = generateToken();
                    authDao.addToken(userID, token, 1);

                    Cookie cookie = new Cookie("token", token);
                    if (remember != null)
                        cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
                    else
                        cookie.setMaxAge(-1);

                    System.out.println("1 age: " + cookie.getMaxAge());

                    resp.addCookie(cookie);
                    resp.sendRedirect("/login");
                    return;
                }
            }
        } else if (stage == 2) {
            int pin_submitted = Integer.parseInt(req.getParameter("pin"));
            Cookie tokenCookie = Util.getCookieByName(req, "token");
            if (tokenCookie != null)
                System.out.println("2 age: " + tokenCookie.getMaxAge());
            int user_id = authDao.authenticate2(tokenCookie.getValue(), pin_submitted);
            if ( user_id != Constants.NO_USER_ID) {
                authDao.deleteToken(tokenCookie.getValue());

                String token = generateToken();

                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(tokenCookie.getMaxAge());
                System.out.println("AGE: " + cookie.getMaxAge());
                resp.addCookie(cookie);
                authDao.addToken(user_id, token, 2);
                resp.sendRedirect("/user");
                return;
            } else {
                resp.sendRedirect("/login");
                return;
            }
        }

        resp.sendError(400);
    }

    private void resendPin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        Cookie tokenCookie = Util.getCookieByName(req, "token");

        if (tokenCookie == null || (authDao.getUserIdByToken(tokenCookie.getValue(), 1) == Constants.NO_USER_ID
                && authDao.getUserIdByToken(tokenCookie.getValue(), 3) == Constants.NO_USER_ID)) {
            resp.sendError(403);
        } else {
            setPin(tokenCookie.getValue());
            req.getRequestDispatcher("/login").forward(req, resp);
        }
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
    public static void generatePin(HttpServletRequest req) {
        Cookie tokenCookie = Util.getCookieByName(req, "token");
        if (tokenCookie == null || new AuthDaoImpl().getPin(tokenCookie.getValue()) != -1)
            return;

        setPin(tokenCookie.getValue());
    }

    private static void setPin(String token) {
        int pin = new Random().nextInt(999999 - 100000 + 1) + 100000;
        new AuthDaoImpl().setPin(token, pin);

        int user_id = new UserDaoImpl().getUserIdByToken(token, 1);
        System.out.println("SHIT: " + user_id);
        if (user_id == Constants.NO_USER_ID)
            user_id = new UserDaoImpl().getUserIdByToken(token, 3);
        User user = new UserDaoImpl().getUserById(user_id);

        Thread thread = new Thread(() -> Util.sendMail(user.getLogin(), String.valueOf(pin)));
        thread.start();
    }

    private String generateToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
