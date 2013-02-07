package com.pace.sfl;


public class IndividualResult {


    public IndividualResult()
    {

    }

    public IndividualResult(int r, double tp)
    {
        this.totalPoints = tp;
        this.setRound(r);
    }

    private double totalPoints;
    private int round;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
