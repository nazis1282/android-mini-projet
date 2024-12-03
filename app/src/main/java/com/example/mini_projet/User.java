package com.example.mini_projet;

import java.util.List;

public class User {
    private String name;
    private String email;
    private String password;
    private List<String> favorites;

    // Constructeur par défaut requis pour Firebase
    public User() {
    }

    // Constructeur personnalisé
    public User(String name, String email, List<String> favorites,String password) {
        this.name = name;
        this.email = email;
        this.password=password;
        this.favorites = favorites;
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
