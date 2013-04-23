package com.pace.sfl.service;

import com.pace.sfl.domain.ZawodnikZuzlowy;
import org.springframework.roo.addon.layers.service.RooService;

import java.util.List;

@RooService(domainTypes = { com.pace.sfl.domain.ZawodnikZuzlowy.class })
public interface ZawodnikZuzlowyService {

    public ZawodnikZuzlowy findZawodnikZuzlowyByPid(int pid);
}
