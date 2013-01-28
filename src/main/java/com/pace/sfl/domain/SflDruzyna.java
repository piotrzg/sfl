package com.pace.sfl.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity
public class SflDruzyna {

    @NotNull
    @Size(max = 31)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ZawodnikZuzlowy> zawodnicy = new HashSet<ZawodnikZuzlowy>();
}
