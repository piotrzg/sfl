package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.TeamWeekResult;
import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONWrappedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RequestMapping("/zawodnikzuzlowys")
@Controller
@RooWebScaffold(path = "zawodnikzuzlowys", formBackingObject = ZawodnikZuzlowy.class)
public class ZawodnikZuzlowyController {

    @Autowired
    ZawodnikZuzlowyService zawodnicy;
    @Autowired
    SflDruzynaService sflDruzynaService;

    @RequestMapping("/zawodnik/{id}")
    public String showPlayer(@PathVariable("id") int zawodnikId, Model uiModel)
    {
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowyByPid(zawodnikId);
        uiModel.addAttribute("zawodnik", zawodnik);
        uiModel.addAttribute("zid", zawodnik.getPid());
        uiModel.addAttribute("tytul", zawodnik.getFname()+" "+zawodnik.getLname());
        return "zawodnikzuzlowys/zawodnik";
    }

    @RequestMapping(value = "/zawodnik/saveIndividualResults/{zid}", method = RequestMethod.POST)
    public @ResponseBody String saveIndResult(@RequestBody String json, @PathVariable("zid") int zid)
    {
        ObjectMapper om = new ObjectMapper();
        try {
            IndividualResult ir = om.readValue(json, IndividualResult.class);
            int round = ir.getRound();
            double tp = Utils.parseBiegiStr(ir.getBiegiStr());

            ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowyByPid(zid);
            IndividualResult individualResult = zawodnik.getWeeklyResults().get(round+2);
            individualResult.setTotalPoints(tp);
            individualResult.setBiegiStr(ir.getBiegiStr());
            zawodnik.getWeeklyResults().set(round+2, individualResult);
            zawodnicy.saveZawodnikZuzlowy(zawodnik);

            List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
            Iterator<SflDruzyna> sflDruzynaIterator = sflDruzynaList.iterator();
            while(sflDruzynaIterator.hasNext())
            {
                SflDruzyna sflDruzyna = sflDruzynaIterator.next();
                Set<ZawodnikZuzlowy> zawodnicySet = sflDruzyna.getZawodnicy();

                if(zawodnicySet.contains(zawodnik))
                {
                    List<TeamWeekResult> teamWeekResultSet = sflDruzyna.getTeamWeekResultList();
                    if(teamWeekResultSet != null)
                    {
                        int weekPts = 0;
                        List<Integer> sklad = sflDruzyna.getSquadForRound(round);
                        if(sklad.size() < 6){
                            teamWeekResultSet.get(round+2).setTotalPoints(0);
                            sflDruzyna.setTeamWeekResultList(teamWeekResultSet);
                            sflDruzynaService.saveSflDruzyna(sflDruzyna);
                            continue;
                        }

                        double skladKSM = 0.0;
                        for(int i=0; i<sklad.size(); i++)
                        {
                            int pid = sklad.get(i);
                            ZawodnikZuzlowy zz = zawodnicy.findZawodnikZuzlowyByPid(pid);
                            skladKSM += zz.getKsm();
                            weekPts += zz.getWeeklyResults().get(round+2).getTotalPoints();
                        }

                        if(skladKSM < 33.0){
                            teamWeekResultSet.get(round+2).setTotalPoints(weekPts/2);
                        }
                        else{
                            teamWeekResultSet.get(round+2).setTotalPoints(weekPts);
                        }
                        sflDruzyna.setTeamWeekResultList(teamWeekResultSet);
                        sflDruzynaService.saveSflDruzyna(sflDruzyna);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"result\":\"OK\"}";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid ZawodnikZuzlowy zawodnikZuzlowy, BindingResult bindingResult,
                         Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, zawodnikZuzlowy);
            return "zawodnikzuzlowys/create";
        }
        uiModel.asMap().clear();

        List<IndividualResult> irs = new ArrayList<IndividualResult>();
        for(int i=-2; i<23;i++)
        {
            IndividualResult ir = new IndividualResult(i);
            irs.add(ir);
        }
        zawodnikZuzlowy.setWeeklyResults(irs);
        zawodnikZuzlowyService.saveZawodnikZuzlowy(zawodnikZuzlowy);
        return "redirect:/zawodnikzuzlowys/" + encodeUrlPathSegment(zawodnikZuzlowy.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new ZawodnikZuzlowy());
        return "zawodnikzuzlowys/create";
    }
}
