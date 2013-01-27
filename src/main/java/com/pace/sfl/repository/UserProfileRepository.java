package com.pace.sfl.repository;

import com.pace.sfl.domain.UserProfile;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = UserProfile.class)
public interface UserProfileRepository {

    List<com.pace.sfl.domain.UserProfile> findAll();
}
