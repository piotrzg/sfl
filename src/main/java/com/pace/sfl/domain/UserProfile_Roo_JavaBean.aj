// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.pace.sfl.domain;

import com.pace.sfl.domain.Account;
import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.domain.UserProfile;

privileged aspect UserProfile_Roo_JavaBean {
    
    public Account UserProfile.getUserAccount() {
        return this.userAccount;
    }
    
    public void UserProfile.setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
    }
    
    public String UserProfile.getCity() {
        return this.city;
    }
    
    public void UserProfile.setCity(String city) {
        this.city = city;
    }
    
    public SflDruzyna UserProfile.getSflDruzyna() {
        return this.sflDruzyna;
    }
    
    public void UserProfile.setSflDruzyna(SflDruzyna sflDruzyna) {
        this.sflDruzyna = sflDruzyna;
    }
    
}
