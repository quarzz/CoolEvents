package by.bsu.fpmi.util;

import by.bsu.fpmi.dao.AuthDao;
import by.bsu.fpmi.dao.AuthDaoImpl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

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

    public static int getAuthStage(HttpServletRequest req) {
        Cookie tokenCookie = Util.getCookieByName(req, "token");
        AuthDao authDao = new AuthDaoImpl();
        if (tokenCookie == null || !authDao.hasToken(tokenCookie.getValue(), 1))
            return 1;
        return 2;
    }

    public static void sendMail(String recipient, String text) {
        final String username = "quarzzap@gmail.com";
        final String password = "googleGr@fgoogle";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }
        );

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("quarzzap@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("SharedNotes PIN code");
            message.setText(text);

            Transport.send(message);
            System.out.println("Message has been sent");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
