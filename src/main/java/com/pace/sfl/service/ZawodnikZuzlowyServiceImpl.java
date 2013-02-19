package com.pace.sfl.service;


import com.pace.sfl.domain.ZawodnikZuzlowy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


public class ZawodnikZuzlowyServiceImpl implements ZawodnikZuzlowyService {

    @Autowired
    MongoTemplate mongoTemplate;


    public ZawodnikZuzlowy findZawodnikZuzlowyByPid(int pid) {
        ZawodnikZuzlowy zawodnik = mongoTemplate.findOne(Query.query(Criteria.where("pid").is(pid)), ZawodnikZuzlowy.class);
        return zawodnik;
    }
}
