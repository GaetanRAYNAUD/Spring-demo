package com.example.demo.controller.privates;

import com.example.demo.controller.dto.AuthMethodDTO;
import com.example.demo.controller.dto.ChangePasswordDTO;
import com.example.demo.controller.dto.TokenDTO;
import com.example.demo.controller.object.ErrorObject;
import com.example.demo.model.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.SessionService;
import com.example.demo.service.definition.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/authentication")
@Tag(name = "Authentication")
public class PrivateAuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateAuthenticationController.class);

    private final JwtService jwtService;

    private final SessionService sessionService;

    private final UserService userService;

    public PrivateAuthenticationController(JwtService jwtService, SessionService sessionService, UserService userService) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @GetMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Refresh a token", description = "Refresh a token to extends it's lifetime",
               security = @SecurityRequirement(name = "Authorization"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed",
                         content = {@Content(schema = @Schema(implementation = TokenDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<TokenDTO> refresh() {
        User user = this.sessionService.getCurrentUser();
        Pair<String, Date> token = this.jwtService.generateToken(user);
        return new ResponseEntity<>(new TokenDTO(token.getKey(), token.getValue(), user.getAuthMethod()), HttpStatus.OK);
    }

    @GetMapping(value = "/authenticated", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Is the user authenticated",
               description = "Simple request to know if the user is authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is authenticated", content = {
                    @Content(schema = @Schema(implementation = AuthMethodDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<AuthMethodDTO> isAuthenticated() {
        User user = this.sessionService.getCurrentUser();
        return new ResponseEntity<>(new AuthMethodDTO(user.getAuthMethod()), HttpStatus.OK);
    }

    @PostMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Change the password of the user",
               description = "Change the password of the user to a new one, invalidate previous tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed"),
            @ApiResponse(responseCode = "500", description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO body) {
        this.userService.changePassword(body.getCurrentPassword(), body.getNewPassword(), body.getPasswordConfirmation());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
