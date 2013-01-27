package com.pace.sfl.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity
public class Account {

    @NotNull
    @Size(min = 6, max = 31)
    private String username;

    @NotNull
    @Size(min = 7, max = 127)
    private String email;

    @NotNull
    @Size(min = 8, max = 127)
    private String password;
}
