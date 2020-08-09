package com.example.demo.service.definition;

import com.example.demo.model.User;

import javax.mail.MessagingException;

public interface UserService extends AbstractEntityService<User> {

    User register(String username, String email, String password, String passwordConfirmation, String token) throws MessagingException;

    void sendActivationMail(String email, String key, String token) throws MessagingException;

    void sendActivationMail(User user) throws MessagingException;

    void activate(String key, String token);

    void resetPassword(String key, String password, String passwordConfirmation, String token);

    User getByEmail(String email);

    User getByUsername(String username);

    void askResetPassword(String email, String token) throws MessagingException;

    User registerGoogle(String username, String tokenId, String token);
}
