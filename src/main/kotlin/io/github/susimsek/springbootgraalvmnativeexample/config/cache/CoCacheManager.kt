package io.github.susimsek.springbootgraalvmnativeexample.config.cache

import com.github.benmanes.caffeine.cache.AsyncCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

/**
 * A generic coroutine-based cache manager that provides asynchronous caching operations.
 */
open class CoCacheManager<K, V>(
    private val cache: AsyncCache<K, V>
) : CacheManager<K, V> {

    /**
     * Retrieves a cached value asynchronously.
     *
     * @param key the key whose associated value is to be returned
     * @return the cached value or null if not present
     */
    override suspend fun get(key: K): V? {
        return cache.getIfPresent(key)?.await()
    }

    /**
     * Puts a value into the cache asynchronously.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be cached
     */
    override suspend fun put(key: K, value: V) {
        cache.put(key, CompletableFuture.completedFuture(value))
    }

    /**
     * Removes a value from the cache.
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    override suspend fun evict(key: K) {
        withContext(Dispatchers.IO) {
            cache.synchronous().invalidate(key)
        }
    }

    /**
     * Clears all entries from the cache.
     */
    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            cache.synchronous().invalidateAll()
        }
    }
}
