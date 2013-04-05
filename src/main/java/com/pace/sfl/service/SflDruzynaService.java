package com.pace.sfl.service;

import com.pace.sfl.domain.SflDruzyna;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.pace.sfl.domain.SflDruzyna.class })
public interface SflDruzynaService {

    public SflDruzyna findByTeamName(String teamName);
    public boolean doesTeamExistIgnoreCase(String teamName);
}
