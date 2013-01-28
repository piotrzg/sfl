package com.pace.sfl.web;

import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/26/13
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CreateTeamController {

    @Autowired
    ZawodnikZuzlowyService zawodnicy;

    @Autowired
    UserProfileService ups;

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

        UserProfile up = ups.findByUsername(name);
        System.out.println("name: "+name);
        System.out.println("up: "+up);
        return "choosePlayers";
    }

    @RequestMapping(value = "/getAllPlayers", produces = "text/html")
    public @ResponseBody String showPlayers()
    {
        List<ZawodnikZuzlowy> listaZawodnikow = zawodnicy.findAllZawodnikZuzlowys();

        StringBuilder sb = new StringBuilder();
        sb.append("{\"aaData\": [");
        for(int i=0; i<listaZawodnikow.size();i++)
        {
            if(i != 0) sb.append(',');
            sb.append("[");
            sb.append('"'+listaZawodnikow.get(i).getLname() +" "+ listaZawodnikow.get(i).getFname()+'"');
            sb.append(',');
            sb.append(listaZawodnikow.get(i).getKsm());
            sb.append(',');
            sb.append("\"<a href='dodajZawodnika?id="+listaZawodnikow.get(i).getId()+"'>Dodaj do skladu</a>\"");
            sb.append("]");
        }
        sb.append("]}");
        System.out.println(sb.toString());
        return sb.toString();
    }

}
