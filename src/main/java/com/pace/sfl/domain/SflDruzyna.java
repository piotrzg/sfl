package com.pace.sfl.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.pace.sfl.TeamWeekResult;
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

    private boolean locked;
    private Set<TeamWeekResult> teamWeekResultList;


    public List<Integer> getSquadForRound(int round)
    {
        Iterator<TeamWeekResult> it = this.teamWeekResultList.iterator();

        while(it.hasNext())
        {
            TeamWeekResult twr = it.next();
            if(twr.getRound() == round)
                return twr.getSklad();
        }

        return null;
    }
}
