package io.github.susimsek.springbootgraalvmnativeexample.exception

/**
 * Exception to be thrown when a resource conflict occurs.
 *
 * @param resourceName the name of the resource.
 * @param searchCriteria the search criteria involved in the conflict.
 * @param searchValue the value causing the conflict.
 */
class ResourceConflictException(
  resourceName: String,
  searchCriteria: String,
  searchValue: Any
) : RuntimeException("The $resourceName already exists with $searchCriteria: $searchValue")
