package fr.xebia.demo.flamingo.service;

public interface ILoginService {

    boolean login(String login, String password);
    void doLogout(String login);
}