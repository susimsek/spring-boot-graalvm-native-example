package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.client.TodoClient
import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

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
}
