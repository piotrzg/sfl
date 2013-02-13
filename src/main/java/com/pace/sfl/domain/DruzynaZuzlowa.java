package com.pace.sfl.domain;

import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RooJavaBean
@RooToString
@RooMongoEntity
public class DruzynaZuzlowa {

    @NotNull
    private String name;

    private int tid;

    private Set<Integer> lockedRounds;

    public int getTid() {
        return this.tid;
    }

    public void setLockedRounds(Set<Integer> lockedRounds) {
        this.lockedRounds = lockedRounds;
    }

    public Set<Integer> getLockedRounds() {
        return this.lockedRounds;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
