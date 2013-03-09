package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.TeamWeekResult;
import com.pace.sfl.domain.DruzynaZuzlowa;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Iterator;
import java.util.List;

@Controller
public class DruzynaWynikiController {



    @Autowired
    UserProfileService ups;

    @Autowired
    SflDruzynaService sflDruzynaService;

    @Autowired
    ZawodnikZuzlowyService zawodnicy;

    @RequestMapping(value = "/druzyna/wyniki/{round}", produces = "text/html")
    public String druzynaWyniki(@PathVariable("round") int round, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        if(sflDruzyna == null)
        {
            //should never happen
        }

        List<Integer> sklad = sflDruzyna.getSquadForRound(round);
        Iterator<Integer> skladIter = sklad.iterator();

        int totalPoints = 0;
        while(skladIter.hasNext())
        {
            Integer pid = skladIter.next();
            if(pid == null)
                continue;

            ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowyByPid(pid);
            if(zawodnik == null)
            {
                //should never happen
                continue;
            }

            IndividualResult ir = zawodnik.getWeeklyResults().get(round+2);
            totalPoints += ir.getTotalPoints();

        }

        uiModel.addAttribute("round", round);
        uiModel.addAttribute("tp", totalPoints);
        uiModel.addAttribute("druzyna", sflDruzyna);

        return "druzynaRundaWynik";

    }
}
