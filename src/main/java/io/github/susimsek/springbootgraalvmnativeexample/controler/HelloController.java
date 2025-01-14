package io.github.susimsek.springbootgraalvmnativeexample.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Hello API", description = "Endpoints for greeting messages")
public class HelloController {

    @Operation(
        summary = "Say Hello",
        description = "Returns a greeting message."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content = @Content(
            mediaType = "text/plain",
            schema = @Schema(implementation = String.class, example = "Hello, GraalVM Native Image!")
        )
    )
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, GraalVM Native Image!";
    }
}
