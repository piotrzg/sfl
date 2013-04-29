package com.pace.sfl.web;

import com.pace.sfl.domain.Account;
import com.pace.sfl.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 4/28/13
 */
@Controller
public class ActivationController {

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/activate/{uuid}", produces = "text/html")
    public String show(Model uiModel, @PathVariable("uuid") String activationId)
    {
        Account acc = accountService.findByActivationId(activationId);
        if(acc != null){
            uiModel.addAttribute("msg", "Aktywacja powiodła się!");
            acc.setActivated(true);
            accountService.updateAccount(acc);
        }
        else{
            uiModel.addAttribute("msg", "Aktywacja nie powiodła się. Sprawdź swój email - tam znajdziesz link aktywacyjny.");
        }

        return "activation";
    }
}
