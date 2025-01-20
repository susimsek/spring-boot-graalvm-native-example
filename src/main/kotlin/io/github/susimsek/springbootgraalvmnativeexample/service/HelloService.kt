package io.github.susimsek.springbootgraalvmnativeexample.service

import io.github.susimsek.springbootgraalvmnativeexample.config.cache.CoCacheManager
import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import io.github.susimsek.springbootgraalvmnativeexample.mapper.HelloMapper
import org.springframework.stereotype.Service

/**
 * Service class for handling greeting messages with caching.
 */
@Service
class HelloService(
    private val helloMapper: HelloMapper,
    private val helloCacheService: CoCacheManager<String, GreetingDTO>
) {
    private val cacheKey = "greeting"

    /**
     * Generates a greeting message with caching in a non-blocking way.
     * Retrieves the greeting from the cache or generates a new one if not found.
     *
     * @return a [GreetingDTO] containing the message.
     */
    suspend fun getGreeting(): GreetingDTO {
        val cachedGreeting: GreetingDTO? = helloCacheService.get(cacheKey)

        if (cachedGreeting != null) {
            return cachedGreeting
        }

        val newGreeting = helloMapper.toGreetingDTO("Hello, GraalVM Native Image!")

        helloCacheService.put(cacheKey, newGreeting)

        return newGreeting
    }
}
