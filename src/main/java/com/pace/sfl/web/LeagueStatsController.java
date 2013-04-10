package com.pace.sfl.web;

import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 4/4/13
 * Time: 11:36 PM
 */
@Controller
public class LeagueStatsController {

    @Autowired
    SflDruzynaService sflDruzynaService;

    @RequestMapping(value = "/liga/stats", produces = "text/html")
    public String getLeagueStats(Model uiModel)
    {
        HashMap<ZawodnikZuzlowy, Integer> zawodnikCounts = new HashMap();

        List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
        Iterator<SflDruzyna> sflDruzynaIterator = sflDruzynaList.iterator();
        while(sflDruzynaIterator.hasNext())
        {
            SflDruzyna sflDruzyna = sflDruzynaIterator.next();
            Set<ZawodnikZuzlowy> zawodnicy = sflDruzyna.getZawodnicy();
            Iterator<ZawodnikZuzlowy> zawodnikZuzlowyIterator = zawodnicy.iterator();
            while(zawodnikZuzlowyIterator.hasNext())
            {
                ZawodnikZuzlowy zz = zawodnikZuzlowyIterator.next();
                Integer zzCount = zawodnikCounts.get(zz);
                if(zzCount != null)
                    zawodnikCounts.put(zz, ++zzCount);
                else
                    zawodnikCounts.put(zz, 1);
            }
        }

        uiModel.addAttribute("zcounts", zawodnikCounts);
        return "ligaStats";
    }
}
