package com.mnewservice.mcontent.domain.formatter;

import com.mnewservice.mcontent.domain.User;
import com.mnewservice.mcontent.manager.UserManager;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Service;

@Service
public class UserFormatter implements Formatter<User> {

    @Autowired
    UserManager userManager;

    @Override
    public String print(User object, Locale locale) {
        return (object != null ? object.getId().toString() : "");
    }

    @Override
    public User parse(String text, Locale locale) throws ParseException {
        Integer id = Integer.valueOf(text);
        return userManager.getUser(id);
    }
}
