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
        System.out.println("username: "+username);
        Account acc = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), Account.class);

        if(acc == null)
        {
            System.out.println("acc0: "+acc);
            acc = mongoTemplate.findOne(Query.query(Criteria.where("email").is(username)), Account.class);
            System.out.println("acc1: "+acc);
            if(acc == null)
                return null;
        }

        UserProfile up = mongoTemplate.findOne(Query.query(Criteria.where("userAccount.username").is(acc.getUsername())), UserProfile.class);
        System.out.println("up: "+up);
        return up;
    }

}
