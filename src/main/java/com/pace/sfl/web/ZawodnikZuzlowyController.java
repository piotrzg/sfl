package com.pace.sfl.web;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public String showPlayer(@RequestParam("id") String zawodnikId, Model uiModel)
    {
        BigInteger bint = new BigInteger(zawodnikId);
        ZawodnikZuzlowy zawodnik = zawodnicy.findZawodnikZuzlowy(bint);
        uiModel.addAttribute("zawodnik", zawodnik);
        return "zawodnik";
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
            IndividualResult ir = new IndividualResult(i, -1);
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
