package com.pace.sfl.web;

import com.pace.sfl.TeamWeekResult;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/26/13
 * Time: 10:13 PM
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
    public String show(Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        if(sflDruzyna == null)
            return "createTeam";
        else
        {
            sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
            Utils.populateModel(uiModel, sflDruzyna);
            return "choosePlayers";
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "saveTeamName", produces = "text/html")
    public String saveTeamName(@RequestParam("teamName") String teamName,
                               final HttpServletRequest request,
                               Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        SflDruzyna sflDruzyna = up.getSflDruzyna();
        if(sflDruzyna == null){
            SflDruzyna checkIfDruzynaExists = sflDruzynaService.findByTeamName(teamName);
            if(checkIfDruzynaExists != null){
                uiModel.addAttribute("errMsg", "Drużyna o tej samej nazwie już istnieje. Wybierz inną nazwe dla swojej drużyny");
                return "createTeam";
            }

            sflDruzyna = new SflDruzyna();
            sflDruzyna.setName(teamName);

            sflDruzynaService.saveSflDruzyna(sflDruzyna);
            up.setSflDruzyna(sflDruzyna);
            ups.saveUserProfile(up);
            return "choosePlayers";
        }
        else{
            return "choosePlayers";
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
        if(sflDruzyna == null)
            return "createTeam";

        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());

        Utils.populateModel(uiModel, sflDruzyna);

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
        else
        {
            //throw exception
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
                sb.append(",\"<a href='dodajZawodnika?id="+listaZawodnikow.get(i).getId()+"'>Dodaj do składu</a>\"");
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
            uiModel.addAttribute("msgToUser", "Skład druzyny nie moze przekraczac "+Constants.MAX_TEAM_PLAYERS+" zawodnikow.");
            Utils.populateModel(uiModel, sflDruzyna);
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

            uiModel.addAttribute("msgToUser", zawodnik.getFname() +" " +zawodnik.getLname()+" dodany do składu!") ;
            uiModel.addAttribute("zawodnikzuzlowy", zawodnik);
        }

        Utils.populateModel(uiModel, sflDruzyna);
        return "choosePlayers";
    }

    @RequestMapping(value = "/usunZawodnika", produces = "text/html")
    public String deletePlayer(@RequestParam("id") String zawodnikId, Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();
        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        if(sflDruzyna.isLocked() ) // and after March 25)
        {
            Utils.populateModel(uiModel, sflDruzyna);
            uiModel.addAttribute("msgToUser", "Upłynął termin możliwości usuwania zawodników. Możesz tylko dodawać zawodników do składu.");
            return "choosePlayers";
        }


        BigInteger bint = new BigInteger(zawodnikId);
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
        HashSet zawodnicySet = (HashSet<ZawodnikZuzlowy>)sflDruzyna.getZawodnicy();
        boolean isRemoved = zawodnicySet.remove(zawodnik);
        sflDruzyna.setZawodnicy(zawodnicySet);
        sflDruzynaService.saveSflDruzyna(sflDruzyna);

        Utils.populateModel(uiModel, sflDruzyna);
        uiModel.addAttribute("msgToUser", zawodnik.getFname() +" " +zawodnik.getLname()+" usunięty ze składu") ;
        return "choosePlayers";
    }


    @RequestMapping(value = "/zglosDruzyne", produces = "text/html")
    public String submitTeam(Model uiModel)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        UserProfile up = ups.findByUsername(name);
        if(up == null)
            return "login";

        SflDruzyna sflDruzyna = up.getSflDruzyna();

        sflDruzyna = sflDruzynaService.findSflDruzyna(sflDruzyna.getId());
        HashSet zawodnicySet = (HashSet<ZawodnikZuzlowy>)sflDruzyna.getZawodnicy();

        String invalidTeamMsg = null;

        boolean isValidTeamBasedOnKSM = Utils.isValidBasedOnKSM(zawodnicySet);
        if(!isValidTeamBasedOnKSM)
            invalidTeamMsg = "Żadna kombinacja składu nie spełnia wymogów KSM.";

        int nrJuniors = Utils.howManyJuniors(zawodnicySet);
        if(nrJuniors < 2)
            invalidTeamMsg = "Sklad nie ma wymaganej liczby juniorow";

        int nrPolish = Utils.howManyPolish(zawodnicySet);
        if(nrPolish < 4)
            invalidTeamMsg = "Sklad nie ma wymaganej liczby Polakow";


        if(invalidTeamMsg == null)
        {
            List<TeamWeekResult> teamWeekResultSet = new ArrayList<TeamWeekResult>();
            for(int i=-2; i<Constants.NR_ROUNDS+1; i++)
            {
                TeamWeekResult twr = new TeamWeekResult(i);
                teamWeekResultSet.add(twr);
            }

            sflDruzyna.setTeamWeekResultList(teamWeekResultSet);
            sflDruzyna.setLocked(true);
            sflDruzynaService.saveSflDruzyna(sflDruzyna);
            return "zarzadzajDruzyna";
        }
        else
        {
            uiModel.addAttribute("druzyna", sflDruzyna);
            uiModel.addAttribute("whyTeamIsInvalidMsg", invalidTeamMsg);
            return "choosePlayers";
        }
    }

}