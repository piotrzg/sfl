package com.pace.sfl.domain;

import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import java.math.BigInteger;

@RooJavaBean
@RooToString
@RooMongoEntity
public class ZawodnikZuzlowy {

    @NotNull
    private String fname;

    @NotNull
    private String lname;

    private double ksm;

    private boolean isJunior;

    private boolean isPolish;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZawodnikZuzlowy that = (ZawodnikZuzlowy) o;

        if (!this.getId().equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (fname+lname+ksm).hashCode();
    }
}
