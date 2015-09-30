package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.UserManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserController {

    private static final Logger LOG
            = Logger.getLogger(UserController.class);

    @Autowired
    private UserManager userManager;

    @RequestMapping({"/user/{id}/activate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void activateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (!user.isActive()) {
            user.setActive(true);
            userManager.saveUser(user);
            return;
        } else {
            throw new IllegalStateException("Provider is already active");
        }
    }

    @RequestMapping({"/user/{id}/deactivate"})
    @ResponseStatus(value = HttpStatus.OK)
    public void deactivateProvider(@PathVariable("id") long id) {
        User user = userManager.getUser(id);
        if (user.isActive()) {
            user.setActive(false);
            userManager.saveUser(user);
            return;
        } else {
            throw new IllegalStateException("Provider is already inactive");
        }
    }

}
