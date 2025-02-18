package com.dwt.photovoltaic.Photovoltaic.System.model;

import javax.validation.constraints.NotNull;

public class AuthRequest {
    @NotNull
    private String username;
     
    @NotNull
    private String password;
 
    // getters and setters are not shown...

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