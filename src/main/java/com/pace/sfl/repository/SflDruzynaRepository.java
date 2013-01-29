package com.pace.sfl.repository;

import com.pace.sfl.domain.SflDruzyna;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = SflDruzyna.class)
public interface SflDruzynaRepository {

    List<com.pace.sfl.domain.SflDruzyna> findAll();
}
