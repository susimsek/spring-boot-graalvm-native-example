package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.mapper.HelloMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class HelloServiceTest {

  private val helloMapper: HelloMapper = mock(HelloMapper::class.java)
  private val helloService = HelloService(helloMapper)

  @Test
  fun `should return greeting message`() {
    runBlocking {
      // Arrange
      val message = "Hello, GraalVM Native Image!"
      val expectedGreeting = GreetingDTO(message)
      `when`(helloMapper.toGreetingDTO(message)).thenReturn(expectedGreeting)

      // Act
      val result = helloService.getGreeting()

      // Assert
      assertEquals(expectedGreeting, result)
      verify(helloMapper, times(1)).toGreetingDTO(message)
    }
  }
}
