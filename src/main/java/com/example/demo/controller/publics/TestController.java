package com.example.demo.controller.publics;

import com.example.demo.controller.object.ErrorObject;
import com.example.demo.controller.dto.TestBodyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Tag(name = "Public")
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping(value = "/test/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Test", description = "This is a test API, to show an example !")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is ok"),
            @ApiResponse(responseCode = "400",
                         description = "Bad request",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> test(@Parameter(description = "Id is a path variable, and is required", required = true)
                                     @PathVariable("id") String id,
                                     @Parameter(description = "requestParam is a query param, and is not required")
                                     @RequestParam(value = "requestParam", required = false) String requestParam) {
        LOGGER.info("Path id: {}", id);
        LOGGER.info("Request param: {}", requestParam);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/test", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Test", description = "This is a test API, to show an example !")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is ok"),
            @ApiResponse(responseCode = "400",
                         description = "Bad request",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))}),
            @ApiResponse(responseCode = "500",
                         description = "Internal error",
                         content = {@Content(schema = @Schema(implementation = ErrorObject.class))})
    })
    public ResponseEntity<Void> testPost(@RequestBody TestBodyDTO body) {
        LOGGER.info("Body: {}", body);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
