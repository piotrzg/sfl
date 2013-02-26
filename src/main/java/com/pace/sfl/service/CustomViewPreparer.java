package com.pace.sfl.service;

import com.pace.sfl.domain.SampleBean;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.repository.AccountRepository;
import com.pace.sfl.service.UserProfileService;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/24/13
 * Time: 9:46 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CustomViewPreparer implements ViewPreparer {

    @Autowired
    UserProfileService ups;
    @Autowired
    AccountService userService;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SampleBean sb;

    @Override
    public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        // Some magic here to get the HttpRequest...
        Object[] requestObjects = tilesContext.getRequestObjects();
        if (requestObjects.length == 2) {
            HttpServletRequest request = (HttpServletRequest) requestObjects[0];
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username

            System.out.println("ups: "+ups);
            System.out.println("userService: "+userService);
            System.out.println("accountRepository: "+accountRepository);
            System.out.println("mongoTemplate: "+mongoTemplate);
            System.out.println("sb: "+sb);
//            UserProfile up = ups.findByUsername(name);
//            request.setAttribute("isLoggedIn", "blskfhkjsdhfjkds");
        }
    }
}