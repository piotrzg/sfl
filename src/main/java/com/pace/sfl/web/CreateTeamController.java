package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import com.pace.sfl.Constants;
import com.pace.sfl.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashSet;
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

    @Autowired
    SflDruzynaService sflDruzynaService;

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
        SflDruzyna sflDruzyna = up.getSflDruzyna();
        if(sflDruzyna == null)
        {
            sflDruzyna = new SflDruzyna();
            sflDruzyna.setName(teamName);
            sflDruzynaService.saveSflDruzyna(sflDruzyna);
            up.setSflDruzyna(sflDruzyna);
            ups.saveUserProfile(up);
            return "choosePlayers";
        }
        else
        {
            System.out.println("WTF dude you already have a team!");
            return "createTeam";
        }

    }

    @RequestMapping(value = "/choosePlayers")
    public String justChoosePlayers(Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());

        double totalKSM = Utils.getTotalKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("totalKSM", totalKSM);
        boolean isValidTeamBasedOnKSM = Utils.isValidBasedOnKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("isValidTeamBasedOnKSM",isValidTeamBasedOnKSM);
        uiModel.addAttribute("druzyna", sflDruzyna);

        return "choosePlayers";
    }

    @RequestMapping(value = "/getAllPlayers")
    public @ResponseBody String showPlayers()
    {
        List<ZawodnikZuzlowy> listaZawodnikow = zawodnicy.findAllZawodnikZuzlowys();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        HashSet<ZawodnikZuzlowy> zawodnicySet = null;
        if(up != null)
        {
            SflDruzyna sflDruzyna = up.getSflDruzyna();
            sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
            zawodnicySet = (HashSet<ZawodnikZuzlowy>)sflDruzyna.getZawodnicy();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"aaData\": [");
        for(int i=0; i<listaZawodnikow.size();i++)
        {
            if(i != 0) sb.append(',');
            sb.append("[");
            sb.append('"'+listaZawodnikow.get(i).getLname() +" "+ listaZawodnikow.get(i).getFname()+'"');
            sb.append(',');
            sb.append(listaZawodnikow.get(i).getKsm());
            if(zawodnicySet == null || !zawodnicySet.contains(listaZawodnikow.get(i))){
                sb.append(",\"<a href='dodajZawodnika?id="+listaZawodnikow.get(i).getId()+"'>Dodaj do skladu</a>\"");
            }
            else{
                sb.append(",\"W skladzie\"");
            }
            sb.append("]");
        }
        sb.append("]}");
        return sb.toString();
    }

    @RequestMapping(value = "/dodajZawodnika", produces = "text/html")
    public String addPlayer(@RequestParam("id") String zawodnikId, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        HashSet<ZawodnikZuzlowy> zawodnicySet = (HashSet<ZawodnikZuzlowy>)sflDruzyna.getZawodnicy();

        if(zawodnicySet.size() >= Constants.MAX_TEAM_PLAYERS)
        {
            uiModel.addAttribute("msgToUser", "Sklad druzyny nie moze przekraczac "+Constants.MAX_TEAM_PLAYERS + " zawodnikow.");
            uiModel.addAttribute("druzyna", sflDruzyna);
            return "choosePlayers";
        }

        BigInteger bint = new BigInteger(zawodnikId);
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
        if(zawodnicySet.contains(zawodnik))
        {
            uiModel.addAttribute("msgToUser", zawodnik.getFname() +" " +zawodnik.getLname()+" juz jest w skladzie");
        }
        else
        {
            zawodnicySet.add(zawodnik);
            sflDruzyna.setZawodnicy(zawodnicySet);
            sflDruzynaService.saveSflDruzyna(sflDruzyna);

            uiModel.addAttribute("msgToUser", zawodnik.getFname() +" " +zawodnik.getLname()+" dodany do skladu!") ;
            uiModel.addAttribute("zawodnikzuzlowy", zawodnik);
        }

        double totalKSM = Utils.getTotalKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("totalKSM", totalKSM);
        boolean isValidTeamBasedOnKSM = Utils.isValidBasedOnKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("isValidTeamBasedOnKSM",isValidTeamBasedOnKSM);
        int nrJuniors = Utils.howManyJuniors((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrJuniors", nrJuniors);
        int nrPolish = Utils.howManyPolish((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrPolish", nrPolish);
        uiModel.addAttribute("druzyna", sflDruzyna);
        return "choosePlayers";
    }

    @RequestMapping(value = "/usunZawodnika", produces = "text/html")
    public String deletePlayer(@RequestParam("id") String zawodnikId, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        SflDruzyna sflDruzyna = up.getSflDruzyna();

        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        BigInteger bint = new BigInteger(zawodnikId);
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
        HashSet zawodnicySet = (HashSet<ZawodnikZuzlowy>)sflDruzyna.getZawodnicy();
        boolean isRemoved = zawodnicySet.remove(zawodnik);
        sflDruzyna.setZawodnicy(zawodnicySet);
        sflDruzynaService.saveSflDruzyna(sflDruzyna);

        double totalKSM = Utils.getTotalKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("totalKSM", totalKSM);
        boolean isValidTeamBasedOnKSM = Utils.isValidBasedOnKSM((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("isValidTeamBasedOnKSM",isValidTeamBasedOnKSM);
        int nrJuniors = Utils.howManyJuniors((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrJuniors", nrJuniors);
        int nrPolish = Utils.howManyPolish((HashSet)sflDruzyna.getZawodnicy());
        uiModel.addAttribute("nrPolish", nrPolish);
        uiModel.addAttribute("druzyna", sflDruzyna);
        return "choosePlayers";
    }

}
