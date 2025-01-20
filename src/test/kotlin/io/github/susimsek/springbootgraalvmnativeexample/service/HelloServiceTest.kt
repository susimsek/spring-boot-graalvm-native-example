package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.config.cache.CoCacheManager
import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.mapper.HelloMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class HelloServiceTest {

    private lateinit var helloMapper: HelloMapper
    private lateinit var helloCacheService: CoCacheManager<String, GreetingDTO>
    private lateinit var helloService: HelloService

    @BeforeEach
    fun setUp() {
        helloMapper = mock()
        helloCacheService = mock()
        helloService = HelloService(helloMapper, helloCacheService)
    }

    @Test
    fun `should return cached greeting when available`() = runBlocking {
        // Arrange
        val cachedGreeting = GreetingDTO("Hello, from cache!")
        whenever(helloCacheService.get("greeting")).thenReturn(cachedGreeting)

        // Act
        val result = helloService.getGreeting()

        // Assert
        assertEquals(cachedGreeting, result)
        verify(helloCacheService).get("greeting")
        verifyNoMoreInteractions(helloMapper, helloCacheService)
    }

    @Test
    fun `should return new greeting and cache it when not available in cache`() = runBlocking {
        // Arrange
        whenever(helloCacheService.get("greeting")).thenReturn(null)
        val newGreeting = GreetingDTO("Hello, GraalVM Native Image!")
        whenever(helloMapper.toGreetingDTO(any())).thenReturn(newGreeting)

        whenever(helloCacheService.put(eq("greeting"), any())).thenReturn(Unit)

        // Act
        val result = helloService.getGreeting()

        // Assert
        assertEquals(newGreeting, result)
        verify(helloCacheService).get("greeting")
        verify(helloMapper).toGreetingDTO("Hello, GraalVM Native Image!")
        verify(helloCacheService).put("greeting", newGreeting)
    }
}
