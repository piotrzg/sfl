package com.pace.sfl;

import com.pace.sfl.domain.ZawodnikZuzlowy;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/11/13
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TeamWeekResult {

    public TeamWeekResult()
    {
        this.locked = false;
        this.totalPoints = 0.0;
    }

    public TeamWeekResult(int r)
    {
        this.round = r;
        this.locked = false;
        this.totalPoints = 0.0;
    }

    private int round;
    private List<ZawodnikZuzlowy> sklad;
    private boolean locked;
    private double totalPoints;
}
