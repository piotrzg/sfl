package com.pace.sfl;

import java.math.BigInteger;
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
        this.setRound(r);
        this.locked = false;
        this.totalPoints = 0.0;
    }

    private int round;
    private List<Integer> sklad;
    private boolean locked;
    private double totalPoints;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamWeekResult that = (TeamWeekResult) o;

        if (getRound() != that.getRound()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getRound();
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public List<Integer> getSklad() {
        return sklad;
    }

    public void setSklad(List<Integer> sklad) {
        this.sklad = sklad;
    }
}
