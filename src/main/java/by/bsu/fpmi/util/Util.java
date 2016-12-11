package by.bsu.fpmi.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class Util {
    public static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static Cookie getCookieByName(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;

        Optional<Cookie> tokenOptional =
                Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("token")).findAny();

        if (tokenOptional.isPresent()) {
            return tokenOptional.get();
        }

        return null;
    }

}
