package com.pace.sfl;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/26/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Role {

    private String id;
    private String role;
    private String roleDescription;

    public void setRole(String r)
    {
        this.role = r;
    }

    public String getRole()
    {
        return this.role;
    }
}
