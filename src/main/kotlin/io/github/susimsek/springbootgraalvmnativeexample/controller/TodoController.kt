package io.github.susimsek.springbootgraalvmnativeexample.controller

import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import io.github.susimsek.springbootgraalvmnativeexample.service.TodoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest Controller for managing todos.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "todos", description = "Endpoints for managing todos")
class TodoController(
    private val todoService: TodoService
) {

    /**
     * GET /todos : Returns a list of todos.
     *
     * @return a Flow stream of [TodoDTO] representing the todo items.
     */
    @Operation(
        summary = "Get Todos",
        description = "Returns a list of todos."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content = [
            Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = ArraySchema(schema = Schema(implementation = TodoDTO::class))
            )
        ]
    )
    @GetMapping("/todos", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodos(): Flow<TodoDTO> {
        return todoService.getTodos()
    }
}
