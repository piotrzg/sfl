package com.pace.sfl.service;


import com.pace.sfl.domain.SflDruzyna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SflDruzynaServiceImpl implements SflDruzynaService {


    @Autowired
    MongoTemplate mongoTemplate;

    public SflDruzyna findByTeamName(String teamName)
    {
        SflDruzyna druzyna = mongoTemplate.findOne(Query.query(Criteria.where("name").is(teamName)), SflDruzyna.class);

        return druzyna;
    }

    public boolean doesTeamExistIgnoreCase(String teamName)
    {
        SflDruzyna druzyna = mongoTemplate.findOne(Query.query(Criteria.where("name").regex('^'+teamName+'$', "i")), SflDruzyna.class);

        if(druzyna != null)
            return true;
        else
            return false;
    }

}
