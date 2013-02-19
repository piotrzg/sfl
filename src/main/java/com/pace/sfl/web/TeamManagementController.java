package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.TeamWeekResult;
import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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

    @Autowired
    ZawodnikZuzlowyService zawodnicy;

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

        List<ZawodnikZuzlowy> notSelected = new ArrayList<ZawodnikZuzlowy>();
        List<Integer> sklad = sflDruzyna.getSquadForRound(round);
        Iterator<Integer> skladIter = sklad.iterator();

        int polishIndex = 1;
        int juniorIndex = 6;
        int foreignIndex = 3;
        while(skladIter.hasNext())
        {
            Integer pid = skladIter.next();
            if(pid == null)
                continue;

            ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowyByPid(pid);
            if(zawodnik.isIsJunior() && juniorIndex < 8)
            {
                uiModel.addAttribute("slot_" + juniorIndex, zawodnik);
                juniorIndex++;
            }
            else if(zawodnik.isIsPolish() && polishIndex < 3)
            {
                uiModel.addAttribute("slot_"+polishIndex, zawodnik);
                polishIndex++;
            }
            else
            {
                uiModel.addAttribute("slot_"+foreignIndex, zawodnik);
                foreignIndex++;
            }
        }


        Iterator<ZawodnikZuzlowy> zawodnicyIter = sflDruzyna.getZawodnicy().iterator();

        while(zawodnicyIter.hasNext())
        {
            ZawodnikZuzlowy zawodnik = zawodnicyIter.next();

            if(!sklad.contains(zawodnik.getPid()))
            {
                notSelected.add(zawodnik);
            }
        }

        uiModel.addAttribute("round", round);
        uiModel.addAttribute("druzyna", sflDruzyna);
        uiModel.addAttribute("notSelected", notSelected);

        return "manageTeam";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/wybierzDruzyne/zapiszDruzyne")
    public String saveTeamForWeek(@RequestBody String json, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        System.out.println("sflDruzyna init:"+sflDruzyna.getTeamWeekResultList());

        ObjectMapper om = new ObjectMapper();
        try {
            TeamWeekResult twrTemp = om.readValue(json, TeamWeekResult.class);

            List<Integer> sklad = twrTemp.getSklad();
//            List<Integer> sklad = new ArrayList<Integer>();

            System.out.println("Sklad:"+sklad);
            System.out.println("twrTemp.getRound():"+twrTemp.getRound());

            Set<TeamWeekResult> teamWeekResultSet = sflDruzyna.getTeamWeekResultList();
            System.out.println("XXX:"+teamWeekResultSet);
            if(teamWeekResultSet != null)
            {
                TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
                twr.setSklad(sklad);
                sflDruzyna.getTeamWeekResultList().remove(twrTemp);
                HashSet xxx = new HashSet(teamWeekResultSet);
                xxx.add(twr);
                sflDruzyna.setTeamWeekResultList(xxx);

                Iterator<TeamWeekResult> twrIter = teamWeekResultSet.iterator();
                boolean found = false;
                while(twrIter.hasNext())
                {
                    TeamWeekResult teamWeekResult = twrIter.next();
                    if(teamWeekResult.getRound() == twrTemp.getRound())
                    {
                        System.out.println("teamWeekResult.getSklad():"+teamWeekResult.getSklad());
//                        System.out.println("B:"+teamWeekResultSet.remove(teamWeekResult));
//                        TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
//                        twr.setSklad(sklad);
//                        System.out.println("A1:"+teamWeekResultSet.size());
//                        teamWeekResultSet.add(twr);
//                        System.out.println("A2:"+teamWeekResultSet.size());
//                        found = true;
                        break;
                    }
                }

//                if(!found)
//                {
//                    TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
//                    twr.setSklad(sklad);
//                    teamWeekResultSet.add(twr);
//                }
            }
            else
            {
                teamWeekResultSet = new HashSet<TeamWeekResult>();
                TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
                twr.setSklad(sklad);
                teamWeekResultSet.add(twr);
                sflDruzyna.setTeamWeekResultList(teamWeekResultSet);
            }


//            sflDruzyna.setTeamWeekResultList(sflDruzyna.get);
            System.out.println("YYY:"+teamWeekResultSet);
            System.out.println("sflDruzyna:"+sflDruzyna.getTeamWeekResultList());
            sflDruzynaService.saveSflDruzyna(sflDruzyna);
//            sflDruzyna.setTeamWeekResultList();


        } catch (IOException e) {
            e.printStackTrace();
        }


        uiModel.addAttribute("druzyna", sflDruzyna);

        return "manageTeam";
    }
}
