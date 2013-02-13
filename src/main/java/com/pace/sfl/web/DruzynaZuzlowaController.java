package com.pace.sfl.web;

import com.pace.sfl.domain.DruzynaZuzlowa;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/druzynazuzlowas")
@Controller
@RooWebScaffold(path = "druzynazuzlowas", formBackingObject = DruzynaZuzlowa.class)
public class DruzynaZuzlowaController {
}
