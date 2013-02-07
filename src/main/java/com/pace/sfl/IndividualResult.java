package com.pace.sfl;


public class IndividualResult {


    public IndividualResult()
    {

    }

    public IndividualResult(int r, double tp)
    {
        this.totalPoints = tp;
        this.round = r;
    }

    private double totalPoints;
    private int round;
}
