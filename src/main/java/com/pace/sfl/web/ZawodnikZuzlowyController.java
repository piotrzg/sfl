package com.pace.sfl.web;

import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.service.ZawodnikZuzlowyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/zawodnikzuzlowys")
@Controller
@RooWebScaffold(path = "zawodnikzuzlowys", formBackingObject = ZawodnikZuzlowy.class)
public class ZawodnikZuzlowyController {

    @Autowired
    ZawodnikZuzlowyService zawodnicy;

    @RequestMapping("/lista")
    public String showAllPlayers()
    {
        return "lista";
    }
}
