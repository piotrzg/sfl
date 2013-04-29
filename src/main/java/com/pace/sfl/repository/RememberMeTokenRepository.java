package com.pace.sfl.repository;

import com.pace.sfl.domain.RememberMeToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 4/24/13
 */
public interface RememberMeTokenRepository extends MongoRepository<RememberMeToken, String> {
    RememberMeToken findBySeries(String series);
    List<RememberMeToken> findByUsername(String username);
}