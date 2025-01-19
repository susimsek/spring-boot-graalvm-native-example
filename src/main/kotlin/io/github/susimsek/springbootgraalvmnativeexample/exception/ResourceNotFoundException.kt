package io.github.susimsek.springbootgraalvmnativeexample.exception

/**
 * Exception to be thrown when a requested resource is not found.
 *
 * @param resourceName the name of the resource.
 * @param searchCriteria the search criteria used to find the resource.
 * @param searchValue the value used in the search.
 */
class ResourceNotFoundException(
    resourceName: String,
    searchCriteria: String,
    searchValue: Any
) : RuntimeException("The $resourceName not found with $searchCriteria: $searchValue")
