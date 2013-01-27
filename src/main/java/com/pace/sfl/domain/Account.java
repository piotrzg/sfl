package com.pace.sfl.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.pace.sfl.Role;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import java.io.Serializable;
import java.util.List;

@RooJavaBean
@RooToString
@RooMongoEntity
public class Account implements Serializable {

    @NotNull
    @Size(min = 6, max = 31)
    private String username;

    @NotNull
    @Size(min = 7, max = 127)
    private String email;

    @NotNull
    @Size(min = 8, max = 127)
    private String password;


    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
