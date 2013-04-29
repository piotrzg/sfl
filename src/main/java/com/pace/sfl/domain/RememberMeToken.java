package com.pace.sfl.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 4/24/13
 */
@Document(collection = "RememberMeToken")
public class RememberMeToken {

    @Id
    private BigInteger id;

    public BigInteger getId() {
        return this.id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    private String username;

    @Indexed
    private String series;

    private String tokenValue;

    private Date date;


    public RememberMeToken(){

    }

    public RememberMeToken(PersistentRememberMeToken token){
        this.setSeries(token.getSeries());
        this.setUsername(token.getUsername());
        this.setTokenValue(token.getTokenValue());
        this.setDate(token.getDate());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}