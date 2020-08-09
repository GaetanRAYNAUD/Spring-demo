package com.example.demo.controller.publics;

import com.example.demo.common.Constants;
import com.example.demo.controller.dto.AskResetPasswordDTO;
import com.example.demo.controller.dto.GoogleUserRegistrationDTO;
import com.example.demo.controller.dto.ResendActivationDTO;
import com.example.demo.controller.dto.ResetPasswordDTO;
import com.example.demo.controller.dto.TokenDTO;
import com.example.demo.controller.dto.UserActivationDTO;
import com.example.demo.controller.dto.UserRegistrationDTO;
import com.example.demo.controller.object.ErrorObject;
import com.example.demo.model.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.definition.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Date;

@RestController
@RequestMapping("/public/authentication")
@Tag(name = "Authentication")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;

    private final JwtService jwtService;

    public AuthenticationController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a user", description = "Register a user, need to provided, username, email, and a *"
                                                          + "pair of matching passwords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User created, a confirmation email has been send to "
                                                             + "the provided email"),
            @ApiResponse(responseCode = "400",
                         description = "Bad request, passwords don't match, or password don't match regex, "
                                       + "or email doesn't match regex",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> register(@RequestBody UserRegistrationDTO registration) throws MessagingException {
        this.userService.register(registration.getUsername(), registration.getEmail(), registration.getPassword(),
                                  registration.getPasswordConfirmation(), registration.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/registration/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a Google user", description = "Register a Google user, need to provided, username and a valid Google TokenId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created, login information are returned"),
            @ApiResponse(responseCode = "400", description = "Bad request, username or email already used",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500", description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<TokenDTO> registerGoogle(@RequestBody GoogleUserRegistrationDTO registration) {
        User user = this.userService.registerGoogle(registration.getUsername(), registration.getTokenId(), registration.getToken());

        Pair<String, Date> token = this.jwtService.generateToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, Constants.BEARER + " " + token);

        return ResponseEntity.ok().headers(headers).body(new TokenDTO(token.getKey(), token.getValue()));
    }

    @PostMapping(value = "/activate", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Activate a user", description = "Activate a user based on an activation key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User activated, the user can now log into the app"),
            @ApiResponse(responseCode = "401",
                         description = "Unauthorized, the key has expired",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "404",
                         description = "Not found, could not find the specified key",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> activate(@RequestBody UserActivationDTO activation) {
        this.userService.activate(activation.getKey(), activation.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/resend-activation", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a user", description = "Register a user, need to provided, username, email, and a *"
                                                          + "pair of matching passwords")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                         description = "A confirmation email has been send to the provided email"),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> resendActivation(@RequestBody ResendActivationDTO resendActivation) throws MessagingException {
        this.userService.sendActivationMail(resendActivation.getEmail(), resendActivation.getKey(), resendActivation.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/ask-reset-password", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reset a password", description = "Send an reset password email to the provided email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request send"),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> askResetPassword(@RequestBody AskResetPasswordDTO resetPassword) throws MessagingException {
        this.userService.askResetPassword(resetPassword.getEmail(), resetPassword.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Change the password", description = "Change the password of the user linked to the reset key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                         description = "Password changed, the user can now log into the app with it's new password"),
            @ApiResponse(responseCode = "401",
                         description = "Unauthorized, the key has expired",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "404",
                         description = "Not found, could not find the specified key",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO resetPassword) {
        this.userService.resetPassword(resetPassword.getKey(),
                                       resetPassword.getPassword(),
                                       resetPassword.getPasswordConfirmation(),
                                       resetPassword.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
