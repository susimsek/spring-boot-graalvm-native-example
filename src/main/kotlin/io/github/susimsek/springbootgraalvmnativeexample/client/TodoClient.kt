package io.github.susimsek.springbootgraalvmnativeexample.client

import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange("/todos")
interface TodoClient {
    @GetExchange
    fun getTodos(): Flow<TodoDTO>
}
