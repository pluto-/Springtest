package com.distributed.springtest.client.forms.register;

import javax.validation.constraints.NotNull;

/**
 * Created by Jonas on 2014-12-16.
 */
public class CreateUserForm {

    @NotNull
    private String username;

    @NotNull
    private String password;


    private String role;

    private String adminPassword;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
