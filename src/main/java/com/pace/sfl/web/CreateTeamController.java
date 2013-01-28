package com.pace.sfl.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/26/13
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CreateTeamController {

    @RequestMapping(value = "/ct", produces = "text/html")
    public String show()
    {
        return "createTeam";
    }


    @RequestMapping(method = RequestMethod.POST, value = "saveTeamName", produces = "text/html")
    public String saveTeamName(@RequestParam("teamName") String teamName,
                               final HttpServletRequest request)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        System.out.println("name: "+name);
        return "choosePlayers";
    }

    @RequestMapping(value = "/getAllPlayers", produces = "text/html")
    public @ResponseBody String showPlayers()
    {
        return "{\"aaData\":[[\"Piotr\", 1984],[\"Adam\", 1985]]}";
    }

}
