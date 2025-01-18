package io.github.susimsek.springbootgraalvmnativeexample.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

class HelloMapperTest {

    private val helloMapper: HelloMapper = Mappers.getMapper(HelloMapper::class.java)

    @Test
    fun `should map string to GreetingDTO`() {
        // Arrange
        val message = "Hello, GraalVM Native Image!"

        // Act
        val greetingDTO = helloMapper.toGreetingDTO(message)

        // Assert
        assertEquals(message, greetingDTO.message)
    }
}
