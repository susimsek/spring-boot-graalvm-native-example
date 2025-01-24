package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.client.TodoClient
import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import io.github.susimsek.springbootgraalvmnativeexample.exception.ResourceNotFoundException
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Service class for handling Todo operations.
 */
@Service
class TodoService(private val todoClient: TodoClient) {
    /**
     * Retrieves the list of todos using the TodoClient.
     *
     * @return a Flow stream of [TodoDTO] representing the todo items.
     */
    fun getTodos(): Flow<TodoDTO> {
        return todoClient.getTodos()
    }

    /**
     * Retrieves a specific todo by its ID using the TodoClient.
     *
     * @param id the ID of the todo item to retrieve.
     * @return a [TodoDTO] representing the requested todo item.
     * @throws ResourceNotFoundException if the todo with the given ID is not found.
     */
    suspend fun getTodoById(id: Long): TodoDTO {
        return try {
            todoClient.getTodoById(id)
        } catch (ex: WebClientResponseException.NotFound) {
            throw ResourceNotFoundException("Todo", "id", id)
        }
    }
}
