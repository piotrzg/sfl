package com.pace.sfl.service;

import com.pace.sfl.domain.DruzynaZuzlowa;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.pace.sfl.domain.DruzynaZuzlowa.class })
public interface DruzynaZuzlowaService {
    public DruzynaZuzlowa findDruzynaZuzlowa(int tid);
}
