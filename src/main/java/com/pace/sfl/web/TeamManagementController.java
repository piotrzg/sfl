package com.pace.sfl.web;

import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/11/13
 * Time: 9:39 PM
 */

@Controller
public class TeamManagementController {

    @Autowired
    UserProfileService ups;

    @Autowired
    SflDruzynaService sflDruzynaService;

    @RequestMapping(value = "/wybierzDruzyne/{round}", produces = "text/html")
    public String chooseTeamForWeek(@PathVariable("round") int round, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());

        uiModel.addAttribute("druzyna", sflDruzyna);

        return "manageTeam";
    }
}
