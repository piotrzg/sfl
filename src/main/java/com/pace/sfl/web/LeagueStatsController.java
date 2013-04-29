package com.pace.sfl.web;

import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @Autowired
    ZawodnikZuzlowyService zawodnicy;
    @Autowired
    UserProfileService ups;

    @RequestMapping(value = "/liga/czesto/zawodnik/{round}", produces = "text/html")
    public String getLeagueStats(Model uiModel, @PathVariable("round") int round)
    {
        HashMap<ZawodnikZuzlowy, Integer> zawodnikCounts = new HashMap();
        List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
        Iterator<SflDruzyna> sflDruzynaIterator = sflDruzynaList.iterator();
        while(sflDruzynaIterator.hasNext())
        {
            SflDruzyna sflDruzyna = sflDruzynaIterator.next();
            if(sflDruzyna.getTeamWeekResultList() != null)
            {
                List<Integer> sklad = sflDruzyna.getSquadForRound(round);
                for(int i=0; i<sklad.size(); i++)
                {
                    int pid = sklad.get(i);
                    ZawodnikZuzlowy zz = zawodnicy.findZawodnikZuzlowyByPid(pid);
                    Integer zzCount = zawodnikCounts.get(zz);
                    if(zzCount != null)
                        zawodnikCounts.put(zz, ++zzCount);
                    else
                        zawodnikCounts.put(zz, 1);
                }
            }
        }

        Map sortedCounts = Utils.sortByValue(zawodnikCounts);
        uiModel.addAttribute("zcounts", sortedCounts);
        uiModel.addAttribute("runda", round);
        return "ligaStatsMostFrequentInSquad";
    }

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


    @RequestMapping(method = RequestMethod.GET, value = "/druzyna/optymalny/sklad/")
    public @ResponseBody String getOptimalSquad()
    {
        UserProfile up = ups.findByUsername(getAuthName());
        if(up == null) return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        if(sflDruzyna == null){
            return "createTeam";
        }

        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        Iterator<ZawodnikZuzlowy> kadraIter = sflDruzyna.getZawodnicy().iterator();

        List<ZawodnikZuzlowy> kadraList = new ArrayList<ZawodnikZuzlowy>();
        while(kadraIter.hasNext())
        {
            ZawodnikZuzlowy zawodnik = kadraIter.next();
            zawodnik = zawodnicy.findZawodnikZuzlowyByPid(zawodnik.getPid());
            kadraList.add(zawodnik);
        }
        // get all combinations
        ICombinatoricsVector<ZawodnikZuzlowy> initialVector = Factory.createVector(kadraList);

        // Create a simple combination generator to generate 3-combinations of the initial vector
        Generator<ZawodnikZuzlowy> gen = Factory.createSimpleCombinationGenerator(initialVector, 7);

        int validSquads = 0;
        int totalSquads = 0;
        HashMap<List<ZawodnikZuzlowy>, Double> validSquadsVector = new HashMap<List<ZawodnikZuzlowy>, Double>();
        // Print all possible combinations
        for (ICombinatoricsVector<ZawodnikZuzlowy> combination : gen) {
            totalSquads++;

            if(Utils.isValidSquad(combination.getVector()))
            {
                validSquads++;
                double pointsForThisCombination = 0.0;
                for(int i=0; i<combination.getVector().size(); i++)
                {
                    pointsForThisCombination += Utils.getTotalPointsInSeason(combination.getVector().get(i));
                }
                validSquadsVector.put(combination.getVector(), pointsForThisCombination);
            }
        }

        Map<List<ZawodnikZuzlowy>, Double> sortedValidSquads = Utils.sortByValue(validSquadsVector);
        Iterator<List<ZawodnikZuzlowy>> sortedValidSquadsIter  = sortedValidSquads.keySet().iterator();
        int i=0;
        StringBuilder sb = new StringBuilder();
        sb.append("{\"msg\": [");
        while(sortedValidSquadsIter.hasNext())
        {
            List<ZawodnikZuzlowy> zz = sortedValidSquadsIter.next();
            if(i==0)
            {
                sb.append("\"" + zz.get(0).getFname() + " " + zz.get(0).getLname() + "\",");
                sb.append("\"" + zz.get(1).getFname() + " " + zz.get(1).getLname() + "\",");
                sb.append("\"" + zz.get(2).getFname() + " " + zz.get(2).getLname() + "\",");
                sb.append("\"" + zz.get(3).getFname() + " " + zz.get(3).getLname() + "\",");
                sb.append("\""+zz.get(4).getFname()+" "+zz.get(4).getLname()+"\",");
                sb.append("\""+zz.get(5).getFname()+" "+zz.get(5).getLname()+"\",");
                sb.append("\""+zz.get(6).getFname()+" "+zz.get(6).getLname()+"\"");
                break;
            }
        }
        sb.append("]}");

        System.out.println("Valid squads: "+validSquads);
        return sb.toString();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/druzyna/wyniki/optymalny/{round}")
    public @ResponseBody String getOptimalSquadForFinishedRound(@PathVariable("round") int round)
    {
        UserProfile up = ups.findByUsername(getAuthName());
        if(up == null) return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        if(sflDruzyna == null){
            return "createTeam";
        }

        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        Iterator<ZawodnikZuzlowy> kadraIter = sflDruzyna.getZawodnicy().iterator();

        List<ZawodnikZuzlowy> kadraList = new ArrayList<ZawodnikZuzlowy>();
        while(kadraIter.hasNext())
        {
            ZawodnikZuzlowy zawodnik = kadraIter.next();
            zawodnik = zawodnicy.findZawodnikZuzlowyByPid(zawodnik.getPid());
            kadraList.add(zawodnik);
        }
        // get all combinations
        ICombinatoricsVector<ZawodnikZuzlowy> initialVector = Factory.createVector(kadraList);

        // Create a simple combination generator to generate 3-combinations of the initial vector
        Generator<ZawodnikZuzlowy> gen = Factory.createSimpleCombinationGenerator(initialVector, 7);

        int validSquads = 0;
        HashMap<List<ZawodnikZuzlowy>, Double> validSquadsVector = new HashMap<List<ZawodnikZuzlowy>, Double>();
        // Print all possible combinations
        for (ICombinatoricsVector<ZawodnikZuzlowy> combination : gen) {

            if(Utils.isValidSquad(combination.getVector()))
            {
                validSquads++;
                double pointsForThisCombination = 0.0;
                for(int i=0; i<combination.getVector().size(); i++)
                {
                    pointsForThisCombination += combination.getVector().get(i).getWeeklyResults().get(round+2).getTotalPoints();
                }
                validSquadsVector.put(combination.getVector(), pointsForThisCombination);
            }
        }

        Map<List<ZawodnikZuzlowy>, Double> sortedValidSquads = Utils.sortByValue(validSquadsVector);
        Iterator<List<ZawodnikZuzlowy>> sortedValidSquadsIter  = sortedValidSquads.keySet().iterator();

        StringBuilder sb = new StringBuilder();
        sb.append("{\"msg\": [");
        int i=0;
        while(sortedValidSquadsIter.hasNext())
        {
            List<ZawodnikZuzlowy> zz = sortedValidSquadsIter.next();
            if(i==0)
            {
                sb.append("\"" + zz.get(0).getFname() + " " + zz.get(0).getLname() + " " + zz.get(0).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(1).getFname() + " " + zz.get(1).getLname() + " " + zz.get(1).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(2).getFname() + " " + zz.get(2).getLname() + " " + zz.get(2).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(3).getFname() + " " + zz.get(3).getLname() + " " + zz.get(3).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(4).getFname() + " " + zz.get(4).getLname() + " " + zz.get(4).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(5).getFname() + " " + zz.get(5).getLname() + " " + zz.get(5).getWeeklyResults().get(round+2).getTotalPoints()+"\",");
                sb.append("\"" + zz.get(6).getFname() + " " + zz.get(6).getLname() + " " + zz.get(6).getWeeklyResults().get(round+2).getTotalPoints()+"\"");
                break;
            }
            i++;
        }
        sb.append("]}");

//        System.out.println("Valid squads: "+validSquads);
        return sb.toString();
    }

    @RequestMapping(value = "/tabelaData")
    public @ResponseBody String tabelaData()
    {
        List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"aaData\": [");
        boolean stringStarted = false;
        for(int i=0; i<sflDruzynaList.size();i++)
        {
            SflDruzyna sflDruzyna = sflDruzynaList.get(i);
            if(sflDruzyna == null || sflDruzyna.getTeamWeekResultList()== null)
                continue;

            double sumPoints =  0.0;
            for(int dindx=3; dindx<sflDruzyna.getTeamWeekResultList().size();dindx++){
                sumPoints += sflDruzyna.getTeamWeekResultList().get(dindx).getTotalPoints();
            }

            if(stringStarted) sb.append(',');

            sb.append("[");
            sb.append('"'+sflDruzyna.getName()+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sflDruzyna.getTeamWeekResultList().get(3).getTotalPoints()).intValue())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sflDruzyna.getTeamWeekResultList().get(5).getTotalPoints()).intValue())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sflDruzyna.getTeamWeekResultList().get(6).getTotalPoints()).intValue())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sflDruzyna.getTeamWeekResultList().get(7).getTotalPoints()).intValue())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sflDruzyna.getTeamWeekResultList().get(8).getTotalPoints()).intValue())+'"');
            sb.append(',');
            sb.append('"'+String.valueOf(((Double)sumPoints).intValue())+'"');
            sb.append(",\"-\"");
            sb.append("]");
            stringStarted = true;
        }
        sb.append("]}");
        return sb.toString();
    }

    private String getAuthName()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName(); //get logged in username
    }
}