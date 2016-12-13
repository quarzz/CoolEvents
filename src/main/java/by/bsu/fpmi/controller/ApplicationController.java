package by.bsu.fpmi.controller;

import by.bsu.fpmi.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ApplicationController extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userID = AccessController.getCurrentUserID(req);

        System.out.println("CURRENT USER ID: " + userID);

        //show profile page for logged in user and redirect unlogged users to login page
        if (userID != Constants.NO_USER_ID)
            resp.sendRedirect("/user");
        else
            resp.sendRedirect("/login");
    }
}
