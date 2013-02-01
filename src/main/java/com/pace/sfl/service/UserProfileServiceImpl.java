package com.pace.sfl.service;


import com.pace.sfl.domain.Account;
import com.pace.sfl.domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    MongoTemplate mongoTemplate;

    public UserProfile findByUsername(String username)
    {
        Account acc = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), Account.class);

        if(acc == null)
            return null;

        UserProfile up = mongoTemplate.findOne(Query.query(Criteria.where("userAccount.username").is(acc.getUsername())), UserProfile.class);
        return up;
    }

}
