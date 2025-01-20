package io.github.susimsek.springbootgraalvmnativeexample.service

import com.github.benmanes.caffeine.cache.AsyncCache
import io.github.susimsek.springbootgraalvmnativeexample.config.cache.CoCacheManager
import io.github.susimsek.springbootgraalvmnativeexample.dto.GreetingDTO
import org.springframework.stereotype.Service

/**
 * Service class for managing greeting messages in cache.
 */
@Service
class HelloCacheService(
    greetingCache: AsyncCache<String, GreetingDTO>
) : CoCacheManager<String, GreetingDTO>(greetingCache)
