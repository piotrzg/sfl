package com.pace.sfl.repository;

import com.pace.sfl.domain.Account;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Account.class)
public interface AccountRepository {

    List<com.pace.sfl.domain.Account> findAll();
}
