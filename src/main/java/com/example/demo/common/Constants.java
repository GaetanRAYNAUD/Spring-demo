package com.example.demo.common;

public final class Constants {

    private Constants() {}

    public static final String BEARER = "Bearer";
    public static final String ROLES_CLAIM = "roles";

    // Mail
    public static final String MAIL_TEMPLATE_FOLDER = "mail/";
    public static final String ACTIVATE_USER_MAIL_TEMPLATE = "ActivateUserTemplate.html";
    public static final String RESET_PASSWORD_MAIL_TEMPLATE = "ResetPasswordTemplate.html";

    //Recaptcha
    public static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
}
