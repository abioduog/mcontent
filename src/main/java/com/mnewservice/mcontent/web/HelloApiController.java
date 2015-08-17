package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.domain.Hello;
import com.mnewservice.mcontent.manager.HelloManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@RestController
public class HelloApiController {

    private static final Logger LOG = Logger.getLogger(HelloApiController.class);

    public static final String TEMPLATE = "Hello, %s!";

    @Autowired
    private HelloManager helloService;

    @RequestMapping("/hello")
    @ResponseBody
    public List<String> hello() {
        LOG.info("/hello");
        Collection<Hello> hellos = helloService.getAll();

        List<String> retval = new ArrayList<>();
        for (Hello hello : hellos) {
            String str = String.format(TEMPLATE, hello.getName());
            LOG.debug(str);
            retval.add(str);
        }

        return retval;
    }
}
