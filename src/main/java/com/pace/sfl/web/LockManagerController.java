package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.domain.DruzynaZuzlowa;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.DruzynaZuzlowaService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/12/13
 * Time: 10:56 PM
 */
@Controller
public class LockManagerController {

    @Autowired
    ZawodnikZuzlowyService zawodnikZuzlowyService;

    @Autowired
    DruzynaZuzlowaService druzyny;

    @RequestMapping(value = "/lockManager/{round}", produces = "text/html")
    public String showLockManager(@PathVariable("round") int round,
                                  Model uiModel)
    {
        uiModel.addAttribute("druzyny", druzyny.findAllDruzynaZuzlowas());
        uiModel.addAttribute("round", round);
        return "lockManager";
    }

    @RequestMapping(value = "/lock/{tid}/{round}", produces = "text/html")
    public String lockTeamForRound(@PathVariable("tid") int tid,
                                   @PathVariable("round") int round, Model uiModel)
    {
        List<ZawodnikZuzlowy> zawodnikZuzlowyList = zawodnikZuzlowyService.findAllZawodnikZuzlowys();

        for(int i=0; i<zawodnikZuzlowyList.size(); i++)
        {
            ZawodnikZuzlowy zawodnik = zawodnikZuzlowyList.get(i);
            if(tid == zawodnik.getTid())
            {
                List<IndividualResult> individualResultList = zawodnik.getWeeklyResults();
                Iterator<IndividualResult> irIterator = individualResultList.iterator();
                while(irIterator.hasNext())
                {
                    IndividualResult ir = irIterator.next();
                    if(ir.getRound() == round)
                    {
                        ir.setLocked(true);
                        zawodnik.getWeeklyResults().set(ir.getRound()+2, ir);
                        zawodnikZuzlowyService.saveZawodnikZuzlowy(zawodnik);
                        break;
                    }
                }
            }
        }

        DruzynaZuzlowa dz = druzyny.findDruzynaZuzlowa(tid);
        if(dz.getLockedRounds() == null)
        {
            Set<Integer> lrs = new HashSet<Integer>();
            dz.setLockedRounds(lrs);
        }

        dz.getLockedRounds().add(new Integer(round));
        dz.setLockedRounds(dz.getLockedRounds());
        druzyny.saveDruzynaZuzlowa(dz);

        uiModel.addAttribute("druzyny", druzyny.findAllDruzynaZuzlowas());
        return "lockManager";

    }


    @RequestMapping(value = "/unlock/{tid}/{round}", produces = "text/html")
    public String unlockTeamForRound(@PathVariable("tid") int tid,
                                   @PathVariable("round") int round, Model uiModel)
    {
        List<ZawodnikZuzlowy> zawodnikZuzlowyList = zawodnikZuzlowyService.findAllZawodnikZuzlowys();

        for(int i=0; i<zawodnikZuzlowyList.size(); i++)
        {
            ZawodnikZuzlowy zawodnik = zawodnikZuzlowyList.get(i);
            if(tid == zawodnik.getTid())
            {
                List<IndividualResult> individualResultList = zawodnik.getWeeklyResults();
                Iterator<IndividualResult> irIterator = individualResultList.iterator();
                while(irIterator.hasNext())
                {
                    IndividualResult ir = irIterator.next();
                    if(ir.getRound() == round)
                    {
                        ir.setLocked(false);
                        zawodnik.getWeeklyResults().set(ir.getRound()+2, ir);
                        zawodnikZuzlowyService.saveZawodnikZuzlowy(zawodnik);
                        break;
                    }
                }
            }
        }

        DruzynaZuzlowa dz = druzyny.findDruzynaZuzlowa(tid);
        if(dz.getLockedRounds() == null)
        {
            Set<Integer> lrs = new HashSet<Integer>();
            dz.setLockedRounds(lrs);
        }

        dz.getLockedRounds().remove(new Integer(round));
        dz.setLockedRounds(dz.getLockedRounds());
        druzyny.saveDruzynaZuzlowa(dz);

        uiModel.addAttribute("druzyny", druzyny.findAllDruzynaZuzlowas());
        return "lockManager";

    }


}
