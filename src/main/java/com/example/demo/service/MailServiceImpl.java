package com.example.demo.service;

import com.example.demo.common.Constants;
import com.example.demo.config.properties.DemoProperties;
import com.example.demo.model.ActivationKey;
import com.example.demo.model.ResetKey;
import com.example.demo.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    protected final JavaMailSender mailSender;

    protected final TemplateEngine templateEngine;

    protected final MessageSource messageSource;

    private final DemoProperties demoProperties;

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, MessageSource messageSource,
                           DemoProperties demoProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.demoProperties = demoProperties;
        ((SpringTemplateEngine) templateEngine).setMessageSource(this.messageSource);
    }

    @Override
    public void sendActivationMail(User user, ActivationKey activationKey) throws MessagingException {
        sendMessageFromTemplate(user.getEmail(),
                                this.messageSource.getMessage("activate_user.subject", null, Locale.FRANCE),
                                Constants.ACTIVATE_USER_MAIL_TEMPLATE,
                                Collections.singletonMap("link", this.demoProperties.getFrontUrl() + "/" +
                                                                 this.demoProperties.getSecurityJwt()
                                                                                    .getActivationPath() +
                                                                 "?key=" + activationKey.getActivationKey()));
    }

    @Override
    public void sendResetPasswordMail(User user, ResetKey resetKey) throws MessagingException {
        sendMessageFromTemplate(user.getEmail(),
                                this.messageSource.getMessage("reset_password.subject", null, Locale.FRANCE),
                                Constants.RESET_PASSWORD_MAIL_TEMPLATE,
                                Collections.singletonMap("link", this.demoProperties.getFrontUrl() + "/" +
                                                                 this.demoProperties.getSecurityJwt()
                                                                                    .getResetPath() +
                                                                 "?key=" + resetKey.getResetKey()));
    }

    @Override
    public void sendMessageFromTemplate(String to, String subject, String templateName,
                                        Map<String, Object> params) throws MessagingException {
        sendMessageWithAttachment(to, subject, buildFromTemplate(templateName, params), null, null);
    }

    @Override
    public void sendMessage(String to, String subject, String content) throws MessagingException {
        sendMessageWithAttachment(to, subject, content, null, null);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String content, String attachmentName,
                                          InputStreamSource attachment) throws MessagingException {
        MimeMessage message = this.mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(this.demoProperties.getMailSender());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        if (attachment != null && StringUtils.isNotBlank(attachmentName)) {
            helper.addAttachment(attachmentName, attachment);
        }

        this.mailSender.send(message);
    }

    protected String buildFromTemplate(String templateName, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        context.setLocale(Locale.FRANCE);
        return this.templateEngine.process(Constants.MAIL_TEMPLATE_FOLDER + templateName, context);
    }
}
