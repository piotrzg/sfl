package com.pace.sfl.repository;

import com.pace.sfl.domain.DruzynaZuzlowa;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = DruzynaZuzlowa.class)
public interface DruzynaZuzlowaRepository {

    List<com.pace.sfl.domain.DruzynaZuzlowa> findAll();
}
