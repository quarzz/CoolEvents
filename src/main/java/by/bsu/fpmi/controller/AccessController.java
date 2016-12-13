package by.bsu.fpmi.controller;

import by.bsu.fpmi.dao.UserDao;
import by.bsu.fpmi.dao.UserDaoImpl;
import by.bsu.fpmi.util.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class AccessController {
    private static UserDao userDao = new UserDaoImpl();

    public static int getCurrentUserID(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null)
            return Constants.NO_USER_ID;

        Optional<Cookie> tokenOptional =
                Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("token")).findAny();

        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get().getValue();
            return userDao.getUserIdByToken(token, 2);
        }

        return Constants.NO_USER_ID;
    }
}
