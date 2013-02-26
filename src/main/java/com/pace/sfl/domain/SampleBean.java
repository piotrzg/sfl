package com.pace.sfl.domain;

import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 2/25/13
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */

@Service("sampleBean")
public class SampleBean {

    private String nazwa;

    public void init()
    {
        this.nazwa = "FalubazHEo";
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
        return nazwa;
    }
}
