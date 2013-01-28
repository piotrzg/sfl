package com.pace.sfl.repository;

import com.pace.sfl.domain.ZawodnikZuzlowy;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = ZawodnikZuzlowy.class)
public interface ZawodnikZuzlowyRepository {

    List<com.pace.sfl.domain.ZawodnikZuzlowy> findAll();
}
