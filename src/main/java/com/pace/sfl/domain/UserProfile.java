package com.pace.sfl.domain;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity
public class UserProfile {

    @NotNull
    @OneToOne
    private Account userAccount;

    @Size(min = 2, max = 127)
    private String city;
}
