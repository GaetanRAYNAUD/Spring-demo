package com.example.demo.service.impl;

import com.example.demo.common.exception.EmailAlreadyExistException;
import com.example.demo.common.exception.EmailNotMatchRegexException;
import com.example.demo.common.exception.InvalidPasswordException;
import com.example.demo.common.exception.KeyExpiredException;
import com.example.demo.common.exception.KeyNotFoundException;
import com.example.demo.common.exception.NotPasswordAccountException;
import com.example.demo.common.exception.PasswordsNotMatchException;
import com.example.demo.common.exception.PasswordsNotMatchRegexException;
import com.example.demo.common.exception.ResetPasswordException;
import com.example.demo.common.exception.UserAlreadyExistException;
import com.example.demo.common.exception.UsernameTooLongException;
import com.example.demo.common.exception.UsernameTooShortException;
import com.example.demo.config.filter.GoogleUserDetails;
import com.example.demo.config.filter.GoogleUserDetailsService;
import com.example.demo.config.properties.DemoProperties;
import com.example.demo.model.ActivationKey;
import com.example.demo.model.AuthMethod;
import com.example.demo.model.ResetKey;
import com.example.demo.model.Role;
import com.example.demo.model.SocialSource;
import com.example.demo.model.SocialUser;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MailService;
import com.example.demo.service.SessionService;
import com.example.demo.service.definition.ActivationKeyService;
import com.example.demo.service.definition.ResetKeyService;
import com.example.demo.service.definition.UserService;
import com.example.demo.service.google.GoogleOpenIdService;
import com.example.demo.service.google.RecaptchaV3Action;
import com.example.demo.service.google.RecaptchaV3Service;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Date;

@Service
@Transactional
public class UserServiceImpl extends AbstractEntityServiceImpl<User, UserRepository> implements UserService, UserDetailsService, GoogleUserDetailsService {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[€@$!%*?&])[A-Za-z\\d€@$!%*?&]{8,}$";

    private static final String EMAIL_REGEX = "(?=^.{6,255}$)(^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$)";

    private final DemoProperties demoProperties;

    private final PasswordEncoder passwordEncoder;

    private final ActivationKeyService activationKeyService;

    private final ResetKeyService resetKeyService;

    private final MailService mailService;

    private final RecaptchaV3Service recaptchaV3Service;

    private final GoogleOpenIdService googleOpenIdService;

    private final SessionService sessionService;

    public UserServiceImpl(UserRepository repository, DemoProperties demoProperties, @Lazy PasswordEncoder passwordEncoder,
                           ActivationKeyService activationKeyService, ResetKeyService resetKeyService, MailService mailService,
                           RecaptchaV3Service recaptchaV3Service, GoogleOpenIdService googleOpenIdService, @Lazy SessionService sessionService) {
        super(repository);
        this.demoProperties = demoProperties;
        this.passwordEncoder = passwordEncoder;
        this.activationKeyService = activationKeyService;
        this.resetKeyService = resetKeyService;
        this.mailService = mailService;
        this.recaptchaV3Service = recaptchaV3Service;
        this.googleOpenIdService = googleOpenIdService;
        this.sessionService = sessionService;
    }

    @Override
    public User register(String username, String email, String password, String passwordConfirmation, String token) throws MessagingException {
        if (username.length() < 8) {
            throw new UsernameTooShortException();
        }

        if (username.length() > 24) {
            throw new UsernameTooLongException();
        }

        if (!password.equals(passwordConfirmation)) {
            throw new PasswordsNotMatchException();
        }

        if (!password.matches(PASSWORD_REGEX)) {
            throw new PasswordsNotMatchRegexException();
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new EmailNotMatchRegexException("Email " + email + " doesn't match regex");
        }

        this.recaptchaV3Service.validateToken(token, email, RecaptchaV3Action.REGISTRATION);

        User user = getByEmail(email);

        if (user != null) {
            throw new EmailAlreadyExistException("Email " + email + " already exists !");
        }

        if (getByUsername(username) != null) {
            throw new UserAlreadyExistException("Username " + username + " already exists !");
        }

        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setEnabled(false);
        user.setRoles(Collections.singleton(Role.ROLE_USER));

        user = create(user);
        sendActivationMail(user);

        return user;
    }

    @Override
    public User registerGoogle(String username, String tokenId, String token) {
        if (username.length() < 8) {
            throw new UsernameTooShortException();
        }

        if (username.length() > 24) {
            throw new UsernameTooLongException();
        }

        this.recaptchaV3Service.validateToken(token, username, RecaptchaV3Action.REGISTRATION);

        Claims claims = this.googleOpenIdService.validateToken(tokenId);
        String email = claims.get("email", String.class);

        if (!email.matches(EMAIL_REGEX)) {
            throw new EmailNotMatchRegexException("Email " + email + " doesn't match regex");
        }

        User user = getByEmail(email);

        if (user != null) {
            throw new EmailAlreadyExistException("Email " + email + " already exists !");
        }

        if (getByUsername(username) != null) {
            throw new UserAlreadyExistException("Username " + username + " already exists !");
        }

        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setGoogleUser(new SocialUser(claims.getSubject(), SocialSource.GOOGLE, user));

        user = create(user);

        return user;
    }

    @Override
    public void sendActivationMail(String email, String key, String token) throws MessagingException {
        this.recaptchaV3Service.validateToken(token, email, RecaptchaV3Action.RESEND_ACTIVATION);

        User user;

        if (StringUtils.isNotBlank(email)) {
            user = getByEmail(email);
        } else {
            ActivationKey activationKey = this.activationKeyService.getByActivationKey(key);
            user = activationKey == null ? null : activationKey.getUser();
        }

        if (user == null || user.isEnabled()) {
            return;
        }

        sendActivationMail(user);
    }

    @Override
    public void sendActivationMail(User user) throws MessagingException {
        if (user == null || user.isEnabled()) {
            return;
        }

        ActivationKey activationKey = this.activationKeyService.create(user);
        this.mailService.sendActivationMail(user, activationKey);
    }

    @Override
    public void activate(String key, String token) {
        this.recaptchaV3Service.validateToken(token, key, RecaptchaV3Action.ACTIVATE);

        ActivationKey activationKey = this.activationKeyService.getByActivationKey(key);

        if (activationKey == null) {
            throw new KeyNotFoundException("Someone tried to activate a user with the key: " + key + " witch does not exists !");
        }

        if (activationKey.getActivationDate().before(DateUtils.addSeconds(new Date(), -this.demoProperties.getSecurityJwt().getActivationExpiration()))) {
            throw new KeyExpiredException("Someone tried to activate a user with the key: " + key + " witch expired !");
        }

        User user = activationKey.getUser();
        user.setEnabled(true);
        update(user);

        this.activationKeyService.delete(activationKey);
    }

    @Override
    public void askResetPassword(String email, String token) throws MessagingException {
        if (!email.matches(EMAIL_REGEX)) {
            throw new ResetPasswordException("Someone ask to reset the password for an email " + email + " that doesn't match regex");
        }

        this.recaptchaV3Service.validateToken(token, email, RecaptchaV3Action.ASK_RESET_PASSWORD);

        User user = getByEmail(email);

        if (user == null) {
            throw new ResetPasswordException("Someone ask to reset the password for an invalid email: " + email + " !");
        }

        ResetKey resetKey = this.resetKeyService.createNewTransaction(user);
        this.mailService.sendResetPasswordMail(user, resetKey);
    }

    @Override
    public void resetPassword(String key, String password, String passwordConfirmation, String token) {
        if (!password.equals(passwordConfirmation)) {
            throw new PasswordsNotMatchException();
        }

        if (!password.matches(PASSWORD_REGEX)) {
            throw new PasswordsNotMatchRegexException();
        }

        this.recaptchaV3Service.validateToken(token, key, RecaptchaV3Action.RESET_PASSWORD);

        ResetKey resetKey = this.resetKeyService.getByResetKey(key);

        if (resetKey == null) {
            throw new KeyNotFoundException("Someone tried to activate a user with the key: "
                                           + key + " witch does not exists !");
        }

        if (resetKey.getResetDate()
                    .before(DateUtils.addSeconds(new Date(), -this.demoProperties.getSecurityJwt()
                                                                                 .getResetExpiration()))) {
            throw new KeyExpiredException("Someone tried to change a password with the key: "
                                          + key + " witch expired !");
        }

        User user = resetKey.getUser();
        user.setPassword(this.passwordEncoder.encode(password));
        update(user);

        this.resetKeyService.delete(resetKey);
    }

    @Override
    public void changePassword(String currentPassword, String newPassword, String passwordConfirmation) {
        if (!newPassword.equals(passwordConfirmation)) {
            throw new PasswordsNotMatchException();
        }

        if (!newPassword.matches(PASSWORD_REGEX)) {
            throw new PasswordsNotMatchRegexException();
        }

        User user = this.sessionService.getCurrentUser();

        if (!AuthMethod.PASSWORD.equals(user.getAuthMethod())) {
            throw new NotPasswordAccountException(user.getUsername() + " tried to change his password but is not a password user !");
        }

        if (!this.passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("");
        }

        user.setPassword(this.passwordEncoder.encode(newPassword));
        user.setPasswordResetDate(new Date());
        update(user);
    }

    @Override
    public User getByEmail(String email) {
        return this.repository.getFirstByEmail(email.toLowerCase());
    }

    @Override
    public User getByUsername(String username) {
        return this.repository.getFirstByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (StringUtils.isBlank(email)) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        User user = getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        return user;
    }

    @Override
    public GoogleUserDetails loadGoogleUserByUsername(String email) throws UsernameNotFoundException {
        if (StringUtils.isBlank(email)) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        User user = getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + email + "' not found");
        }

        return user;
    }
}
