package io.github.susimsek.springbootgraalvmnativeexample.client

import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

/**
 * Client interface for interacting with the Todo API.
 */
@HttpExchange("/todos")
interface TodoClient {
    /**
     * Fetches a list of todos from the remote API.
     *
     * @return a Flow stream of [TodoDTO] representing the todo items.
     */
    @GetExchange
    fun getTodos(): Flow<TodoDTO>
}
