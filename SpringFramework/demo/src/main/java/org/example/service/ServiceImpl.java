package org.example.service;

public class ServiceImpl implements Service {

    private String username;

    public void save(){
        System.out.println("service...save..."+username);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
