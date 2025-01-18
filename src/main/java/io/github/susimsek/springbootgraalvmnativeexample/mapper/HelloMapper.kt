package io.github.susimsek.springbootgraalvmnativeexample.mapper

import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import org.mapstruct.Mapper

/**
 * Mapper for converting string message to GreetingDTO.
 */
@Mapper(componentModel = "spring")
interface HelloMapper {

    /**
     * Converts a string message to a GreetingDTO.
     *
     * @param message the message to be converted.
     * @return the resulting GreetingDTO.
     */
    fun toGreetingDTO(message: String): GreetingDTO
}
