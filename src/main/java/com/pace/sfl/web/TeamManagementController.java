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
import org.springframework.web.bind.annotation.*;

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
            if(zawodnik.getWeeklyResults() == null)
            {
                List<IndividualResult> irs = new ArrayList<IndividualResult>();
                for(int i=-2; i<23;i++)
                {
                    IndividualResult ir = new IndividualResult(i);
                    irs.add(ir);
                }
                zawodnik.setWeeklyResults(irs);
                System.out.println("Savings irs for: "+zawodnik.getLname());
                zawodnicy.saveZawodnikZuzlowy(zawodnik);
            }

            boolean isLocked = zawodnik.getWeeklyResults().get(round+2).isLocked();
            if(zawodnik.isIsJunior() && juniorIndex < 8)
            {
                uiModel.addAttribute("slot_" + juniorIndex, zawodnik);
                String css = isLocked ? "junior moveable locked" : "junior moveable";
                uiModel.addAttribute("slot_" + juniorIndex+"_css", css);
                juniorIndex++;
            }
            else if(zawodnik.isIsPolish() && polishIndex < 3)
            {
                uiModel.addAttribute("slot_"+polishIndex, zawodnik);
                String css = isLocked ? "polish moveable locked" : "polish moveable";
                uiModel.addAttribute("slot_" + polishIndex+"_css", css);
                polishIndex++;
            }
            else
            {
                uiModel.addAttribute("slot_"+foreignIndex, zawodnik);
                String css = isLocked ? "foreign moveable locked" : "foreign moveable";
                uiModel.addAttribute("slot_" + foreignIndex+"_css", css);
                foreignIndex++;
            }
        }


        List<ZawodnikZuzlowy> notSelected = new ArrayList<ZawodnikZuzlowy>();
        Iterator<ZawodnikZuzlowy> zawodnicyIter = sflDruzyna.getZawodnicy().iterator();
        while(zawodnicyIter.hasNext())
        {
            ZawodnikZuzlowy zawodnik = zawodnicyIter.next();

            if(!sklad.contains(zawodnik.getPid())){
                zawodnik = zawodnicy.findZawodnikZuzlowyByPid(zawodnik.getPid());
                notSelected.add(zawodnik);
            }
        }

        uiModel.addAttribute("round", round);
        uiModel.addAttribute("druzyna", sflDruzyna);
        uiModel.addAttribute("notSelected", notSelected);

        return "manageTeam";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/wybierzDruzyne/zapiszDruzyne")
    public @ResponseBody String saveTeamForWeek(@RequestBody String json, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
        {
            return "{\"msg\":\"login\"}";
        }

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());

        ObjectMapper om = new ObjectMapper();
        try {
            TeamWeekResult twrTemp = om.readValue(json, TeamWeekResult.class);

            List<Integer> sklad = twrTemp.getSklad();

            System.out.println("Sklad:"+sklad);
            System.out.println("twrTemp.getRound():"+twrTemp.getRound());

            Set<TeamWeekResult> teamWeekResultSet = sflDruzyna.getTeamWeekResultList();
            if(teamWeekResultSet != null)
            {
                TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
                twr.setSklad(sklad);
                sflDruzyna.getTeamWeekResultList().remove(twrTemp);
                HashSet xxx = new HashSet(teamWeekResultSet);
                xxx.add(twr);
                sflDruzyna.setTeamWeekResultList(xxx);
            }
            else
            {
                teamWeekResultSet = new HashSet<TeamWeekResult>();
                TeamWeekResult twr = new TeamWeekResult(twrTemp.getRound());
                twr.setSklad(sklad);
                teamWeekResultSet.add(twr);
                sflDruzyna.setTeamWeekResultList(teamWeekResultSet);
            }

            sflDruzynaService.saveSflDruzyna(sflDruzyna);

            Iterator squadIter = sklad.iterator();
            int howManyInSquad = 0;
            int howManyJuniors = 0;
            int howManyPolish = 0;
            double totalKSM = 0.0;
            double minKSM = 100.0;
            while(squadIter.hasNext())
            {
                Integer i = (Integer)squadIter.next();
                if(i != null)
                {
                    howManyInSquad++;
                    ZawodnikZuzlowy zz = zawodnicy.findZawodnikZuzlowyByPid(i);
                    if(zz.isIsJunior())
                        howManyJuniors++;

                    if(zz.isIsPolish())
                        howManyPolish++;

                    totalKSM += zz.getKsm();

                    if(zz.getKsm() < minKSM)
                        minKSM = zz.getKsm();
                }
            }

            String resp = "{\"msg\": \"OK\"}";
            if(howManyInSquad < 6)
                return "{\"msg\":\"Druzyna musi miec conajmniej 6 zawodnikow w skladzie\"}";

            if(howManyJuniors < 2)
                return "{\"msg\":\"Druzyna musi miec conajmniej 2 juniorow w skladzie\"}";

            if(howManyPolish < 4)
                return "{\"msg\":\"Druzyna musi miec conajmniej 4 Polakow w skladzie\"}";


            if(minKSM != 100 && howManyInSquad == 7)
                totalKSM = totalKSM - minKSM;

            if(totalKSM > 40.0)
                return "{\"msg\":\"KSM druzyny jest za wysoki. Obniz KSM druzyny\"}";

            if(totalKSM < 33.0)
                return "{\"msg\":\"KSM druzyny jest za niski. Podwyz KSM druzyny\"}";

            return resp;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"msg\":\"Zmiany zapisane!\"}";
//        return "manageTeam";
    }
}