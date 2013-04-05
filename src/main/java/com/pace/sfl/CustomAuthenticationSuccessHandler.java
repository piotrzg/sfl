package com.pace.sfl;

import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 4/4/13
 * Time: 12:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    UserProfileService ups;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        // use authentication object to fetch the user object and update its timestamp
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        up.setLastLogin(System.currentTimeMillis());
        System.out.println("Login IP: "+request.getRemoteAddr());
        ups.saveUserProfile(up);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
