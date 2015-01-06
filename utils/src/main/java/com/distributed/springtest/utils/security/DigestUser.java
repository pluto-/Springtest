package com.distributed.springtest.utils.security;

/**
 * Created by Jonas on 2015-01-06.
 */
public class DigestUser {

    private String username;
    private String hashedPassword;
    private int nc;

    public void setNc(int nc) {
        this.nc = nc;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public int getNc() {
        return nc;
    }

    public DigestUser(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.nc = 0;
    }
}
