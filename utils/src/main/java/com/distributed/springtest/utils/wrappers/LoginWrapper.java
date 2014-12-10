package com.distributed.springtest.utils.wrappers;

/**
 * Created by Jonas on 2014-12-10.
 */
public class LoginWrapper {

    private String username;

    public LoginWrapper() {
    }

    public void setEncryptedPassword(String encryptedPassword) {

        this.encryptedPassword = encryptedPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String encryptedPassword;

    public LoginWrapper(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getUsername() {
        return username;
    }
}
