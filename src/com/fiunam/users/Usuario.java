package com.fiunam.users;

/**
 * Base pasa la creación de un usuario con nombre de
 * usuario y contraseña
 */
public abstract class Usuario {
    private String username;
    private String password;

    public Usuario() {

    }

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
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

    public Usuario getCurrentUser(){
        return this;
    }

    @Override
    public String toString() {
        return "User: " +  this.username + " | Password:  " + "*".repeat(this.password.length());
    }
}
