package io.github.susimsek.springbootgraalvmnativeexample.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Data Transfer Object for Todos.
 */
@Schema(description = "Data Transfer Object for Todos.")
data class TodoDTO(
    @Schema(description = "The unique identifier of the todo.", example = "1")
    val id: Int?,

    @Schema(description = "The title of the todo.", example = "Buy groceries")
    val title: String,

    @Schema(description = "The completion status of the todo.", example = "false")
    val completed: Boolean
)
