package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.ZawodnikZuzlowy;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/zawodnikzuzlowys")
@Controller
@RooWebScaffold(path = "zawodnikzuzlowys", formBackingObject = ZawodnikZuzlowy.class)
public class ZawodnikZuzlowyController {

    @Autowired
    ZawodnikZuzlowyService zawodnicy;

    @RequestMapping("/zawodnik/{id}")
    public String showPlayer(@PathVariable("id") String zawodnikId, Model uiModel)
    {
        BigInteger bint = new BigInteger(zawodnikId);
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
        uiModel.addAttribute("zawodnik", zawodnik);
        uiModel.addAttribute("zid", zawodnik.getId().toString());
        return "zawodnikzuzlowys/zawodnik";
    }

    @RequestMapping(
            value = "/zawodnik/saveIndividualResults/{zid}", method = RequestMethod.POST)
    public @ResponseBody String saveIndResult(@RequestBody String json, @PathVariable("zid") String zid)
    {
        ObjectMapper om = new ObjectMapper();
        try {
            IndividualResult ir = om.readValue(json, IndividualResult.class);
            double tp = Utils.parseBiegiStr(ir.getBiegiStr());

            System.out.println("Total Points: "+tp);

            BigInteger bint = new BigInteger(zid);
            ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
            ir.setTotalPoints(tp);
            zawodnik.getWeeklyResults().set(ir.getRound()+2, ir);
            zawodnicy.saveZawodnikZuzlowy(zawodnik);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

        System.out.println("create ZZ:"+zawodnikZuzlowy);
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
