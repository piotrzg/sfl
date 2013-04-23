package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.domain.DruzynaZuzlowa;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.DruzynaZuzlowaService;
import com.pace.sfl.service.SflDruzynaService;
import com.pace.sfl.service.UserProfileService;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/12/13
 */
@Controller
public class LockManagerController {

    @Autowired
    ZawodnikZuzlowyService zawodnikZuzlowyService;

    @Autowired
    DruzynaZuzlowaService druzyny;

    @Autowired
    SflDruzynaService sflDruzynaService;

    @Autowired
    UserProfileService ups;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage templateMessage;

    @RequestMapping(value = "/lockManager/{round}", produces = "text/html")
    public String showLockManager(@PathVariable("round") int round,
                                  Model uiModel)
    {
        uiModel.addAttribute("isLoggedIn", "yes");
        uiModel.addAttribute("druzyny", druzyny.findAllDruzynaZuzlowas());
        uiModel.addAttribute("round", round);
        return "lockManager";
    }

    @RequestMapping(value = "/sendReminders/{round}", produces = "text/html")
    public String sendReminders(@PathVariable("round") int round)
    {
        List<SflDruzyna> sflDruzynaList = sflDruzynaService.findAllSflDruzynas();
        Iterator<SflDruzyna> sflDruzynaIterator = sflDruzynaList.iterator();
        while(sflDruzynaIterator.hasNext())
        {
            SflDruzyna sflDruzyna = sflDruzynaIterator.next();
            List<Integer> sklad = sflDruzyna.getSquadForRound(round);

            if(sklad == null){
                UserProfile up = mongoTemplate.findOne(Query.query(Criteria.where("sflDruzyna.name").is(sflDruzyna.getName())), UserProfile.class);
                System.out.println(up.getUserAccount().getEmail()+": "+"Nie zglosiles");
                SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
                msg.setTo(up.getUserAccount().getEmail());
                msg.setSubject("SpeedwayFantasy.pl - nie zapomnij zgłosić swojej drużyny!");
                msg.setText("Nie zgłosiłeś jeszcze swojej drużyny do rywalizacji na speedwayFantasy.pl" +
                        " Zaloguj się aby zgłosić drużynę" +
                        " i zacznij zdobywać punkty.\nhttp://speedwayfantasy.pl/choosePlayers");
                try{
                    this.mailSender.send(msg);
                    Thread.sleep(1500);
                }
                catch(MailException ex) {
                    System.err.println(ex.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                continue;
            }

            int howManyInSquad = 0;
            int howManyJuniors = 0;
            int howManyPolish = 0;
            double totalKSM = 0.0;
            double minKSM = 100.0;
            Iterator<Integer> skladIter = sklad.iterator();
            while(skladIter.hasNext())
            {
                Integer pid = skladIter.next();
                if(pid == null)
                    continue;

                ZawodnikZuzlowy zz = zawodnikZuzlowyService.findZawodnikZuzlowyByPid(pid);
                if(zz == null){
                    continue;
                }

                howManyInSquad++;
                if(zz.isIsJunior())
                    howManyJuniors++;

                if(zz.isIsPolish())
                    howManyPolish++;

                totalKSM += zz.getKsm();

                if(zz.getKsm() < minKSM)
                    minKSM = zz.getKsm();
            }

            String emailMsg = null;
            emailMsg = getEmailMsg(howManyInSquad, howManyJuniors, howManyPolish, totalKSM, minKSM);

            if(emailMsg != null)
            {
                UserProfile up = mongoTemplate.findOne(Query.query(Criteria.where("sflDruzyna.name").is(sflDruzyna.getName())), UserProfile.class);
                System.out.println(up.getUserAccount().getEmail()+": "+emailMsg);
                SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
                msg.setTo(up.getUserAccount().getEmail());
                msg.setSubject("SpeedwayFantasy.pl - skład na "+round+" rundę nie spełnia wymagań");
                emailMsg += "\n\nPrzejdź do http://speedwayfantasy.pl/wybierzDruzyne/"+round+" aby wybrać skład na "+round+" rundę";
                msg.setText(emailMsg);
                try{
                    this.mailSender.send(msg);
                    Thread.sleep(1500);
                }
                catch(MailException ex) {
                    System.err.println(ex.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        return "lockManager";
    }


    private String getEmailMsg(int howManyInSquad, int howManyJuniors, int howManyPolish, double totalKSM, double minKSM) {
        if(howManyInSquad < 6)
            return "Druzyna musi miec conajmniej 6 zawodników w składzie. Zgodnie z regulaminem, aktualny skład nie zdobędzie punktów na daną rundę";

        if(minKSM != 100 && howManyInSquad == 7)
            totalKSM = totalKSM - minKSM;

        if(totalKSM > 40.0)
            return "KSM drużyny jest za wysoki. Obniż KSM drużyny. Zgodnie z regulaminem, aktualny skład nie zdobędzie punktów na daną rundę";

        if(totalKSM < 33.0)
            return "KSM drużyny jest za niski. Podwyż KSM drużyny. Zgodnie z regulaminem, aktualny skład zdobędzie tylko połowę punktów wywalczonych przez zawodników w danej rundzie";

        if(howManyJuniors < 2)
            return "Drużyna musi miec conajmniej 2 juniorów w składzie.";

        if(howManyPolish < 4)
            return "Drużyna musi miec conajmniej 4 Polaków w składzie.";

        return null;
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


    @RequestMapping(value = "/lock/all/{round}", produces = "text/html")
    public String lockAllTeamsForRound(@PathVariable("round") int round, Model uiModel)
    {
        List<ZawodnikZuzlowy> zawodnikZuzlowyList = zawodnikZuzlowyService.findAllZawodnikZuzlowys();

        for(int i=0; i<zawodnikZuzlowyList.size(); i++)
        {
            ZawodnikZuzlowy zawodnik = zawodnikZuzlowyList.get(i);
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

        List<DruzynaZuzlowa> druzynaZuzlowaList = druzyny.findAllDruzynaZuzlowas();
        for(int i=0; i<druzynaZuzlowaList.size();i++)
        {
            DruzynaZuzlowa dz = druzynaZuzlowaList.get(i);
            if(dz.getLockedRounds() == null)
            {
                Set<Integer> lrs = new HashSet<Integer>();
                dz.setLockedRounds(lrs);
            }

            dz.getLockedRounds().add(new Integer(round));
            dz.setLockedRounds(dz.getLockedRounds());
            druzyny.saveDruzynaZuzlowa(dz);
        }

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
                if(individualResultList == null)
                {
                    List<IndividualResult> irs = new ArrayList<IndividualResult>();
                    for(int y=-2; y<23;y++)
                    {
                        IndividualResult ir = new IndividualResult(y);
                        irs.add(ir);
                    }
                    zawodnik.setWeeklyResults(irs);
                    System.out.println("Savings irs for: "+zawodnik.getLname());
                    zawodnikZuzlowyService.saveZawodnikZuzlowy(zawodnik);
                }

                individualResultList = zawodnik.getWeeklyResults();
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