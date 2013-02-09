package com.pace.sfl;


public class IndividualResult {


    public IndividualResult()
    {

    }

    public IndividualResult(int r, double tp)
    {
        this.setTotalPoints(tp);
        this.setRound(r);
    }

    private double totalPoints;
    private int round;
    private String biegiStr;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getBiegiStr() {
        return biegiStr;
    }

    public void setBiegiStr(String biegiStr) {
        this.biegiStr = biegiStr;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }
}
