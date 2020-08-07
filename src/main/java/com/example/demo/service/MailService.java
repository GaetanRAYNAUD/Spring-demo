package com.example.demo.service;

import com.example.demo.model.ActivationKey;
import com.example.demo.model.ResetKey;
import com.example.demo.model.User;
import org.springframework.core.io.InputStreamSource;

import javax.mail.MessagingException;
import java.util.Map;

public interface MailService {

    void sendActivationMail(User user, ActivationKey activationKey) throws MessagingException;

    void sendResetPasswordMail(User user, ResetKey resetKey) throws MessagingException;

    void sendMessageFromTemplate(String to, String subject, String templateName, Map<String, Object> params) throws MessagingException;

    void sendMessage(String to, String subject, String content) throws MessagingException;

    void sendMessageWithAttachment(String to, String subject, String content, String attachmentName, InputStreamSource attachment) throws MessagingException;
}
