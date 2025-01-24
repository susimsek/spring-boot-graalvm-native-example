package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.client.TodoClient
import io.github.susimsek.springbootgraalvmnativeexample.dto.TodoDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class TodoService(private val todoClient: TodoClient) {
    fun getTodos(): Flow<TodoDTO> {
        return todoClient.getTodos()
    }
}
