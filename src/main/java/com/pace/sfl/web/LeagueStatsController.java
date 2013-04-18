package com.pace.sfl.web;

import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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

        Map sortedCounts = Utils.sortByValue(zawodnikCounts);
        uiModel.addAttribute("zcounts", sortedCounts);
        return "ligaStats";
    }


    @RequestMapping(value = "/tabela")
    public String tabela()
    {
        return "tabela";
    }


    @RequestMapping(value = "/tabelaData")
    public @ResponseBody String tabelaData()
    {
        List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"aaData\": [");
        for(int i=0; i<sflDruzynaList.size();i++)
        {
            SflDruzyna sflDruzyna = sflDruzynaList.get(i);
            if(sflDruzyna == null || sflDruzyna.getTeamWeekResultList()== null)
                continue;

            double sumPoints =  sflDruzyna.getTeamWeekResultList().get(3).getTotalPoints()+
                    sflDruzyna.getTeamWeekResultList().get(4).getTotalPoints()+
                    sflDruzyna.getTeamWeekResultList().get(5).getTotalPoints()+
                    sflDruzyna.getTeamWeekResultList().get(6).getTotalPoints();
            if(i != 0) sb.append(',');
            sb.append("[");

            sb.append('"'+sflDruzyna.getName()+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(sflDruzyna.getTeamWeekResultList().get(3).getTotalPoints())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(sflDruzyna.getTeamWeekResultList().get(4).getTotalPoints())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(sflDruzyna.getTeamWeekResultList().get(5).getTotalPoints())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(sflDruzyna.getTeamWeekResultList().get(6).getTotalPoints())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(sumPoints)+'"');
            sb.append(",\"-\"");
            sb.append("]");
        }
        sb.append("]}");
        return sb.toString();
    }
}