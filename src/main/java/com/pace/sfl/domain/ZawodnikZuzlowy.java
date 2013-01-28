package com.pace.sfl.domain;

import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity
public class ZawodnikZuzlowy {

    @NotNull
    private String fname;

    @NotNull
    private String lname;

    private double ksm;
}
