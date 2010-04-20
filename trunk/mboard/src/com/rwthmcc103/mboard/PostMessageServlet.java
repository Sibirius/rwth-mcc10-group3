package com.rwthmcc103.mboard;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class PostMessageServlet {
	private static final Logger log = Logger.getLogger(PostMessageServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        if (content == null) {
            content = "(No message)";
        }
        if (user != null) {
            log.info("Message posted by user " + user.getNickname() + ": " + content);
        } else {
            log.info("Message posted anonymously: " + content);
        }
        resp.sendRedirect("/mboard.jsp");
    }
}
