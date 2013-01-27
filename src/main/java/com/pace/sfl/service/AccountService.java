package com.pace.sfl.service;

import com.pace.sfl.domain.Account;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.pace.sfl.domain.Account.class })
public interface AccountService {

    public Account findByUsername(String username);
}
