// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.pace.sfl.domain;

import com.pace.sfl.IndividualResult;
import com.pace.sfl.domain.ZawodnikZuzlowy;

privileged aspect ZawodnikZuzlowy_Roo_JavaBean {
    
    public String ZawodnikZuzlowy.getFname() {
        return this.fname;
    }
    
    public void ZawodnikZuzlowy.setFname(String fname) {
        this.fname = fname;
    }
    
    public String ZawodnikZuzlowy.getLname() {
        return this.lname;
    }
    
    public void ZawodnikZuzlowy.setLname(String lname) {
        this.lname = lname;
    }
    
    public double ZawodnikZuzlowy.getKsm() {
        return this.ksm;
    }
    
    public void ZawodnikZuzlowy.setKsm(double ksm) {
        this.ksm = ksm;
    }
    
    public boolean ZawodnikZuzlowy.isIsJunior() {
        return this.isJunior;
    }
    
    public void ZawodnikZuzlowy.setIsJunior(boolean isJunior) {
        this.isJunior = isJunior;
    }
    
    public boolean ZawodnikZuzlowy.isIsPolish() {
        return this.isPolish;
    }
    
    public void ZawodnikZuzlowy.setIsPolish(boolean isPolish) {
        this.isPolish = isPolish;
    }
    
    public IndividualResult ZawodnikZuzlowy.getWeeklyResults() {
        return this.weeklyResults;
    }
    
    public void ZawodnikZuzlowy.setWeeklyResults(IndividualResult weeklyResults) {
        this.weeklyResults = weeklyResults;
    }
    
}
