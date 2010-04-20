package com.rwthmcc103.mboard;

import java.io.IOException;
import java.util.Date;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.rwthmcc103.mboard.Message;
import com.rwthmcc103.mboard.PMF;

@SuppressWarnings("serial")
public class PostMessageServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = req.getParameter("content");
        Date date = new Date();
        Message message = new Message(user, content, date);
        
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(message);
        } finally {
            pm.close();
        }
        
        resp.sendRedirect("/mboard.jsp");
    }
}
