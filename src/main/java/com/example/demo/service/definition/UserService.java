package com.example.demo.service.definition;

import com.example.demo.model.User;

import javax.mail.MessagingException;

public interface UserService extends AbstractEntityService<User> {

    User register(String username, String email, String password, String passwordConfirmation) throws MessagingException;

    void sendActivationMail(String email, String key) throws MessagingException;

    void sendActivationMail(User user) throws MessagingException;

    void activate(String key);

    void resetPassword(String key, String password, String passwordConfirmation);

    User getByEmail(String email);

    User getByUsername(String username);

    void resetPassword(String email) throws MessagingException;
}
