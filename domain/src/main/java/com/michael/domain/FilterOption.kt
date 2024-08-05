package com.michael.domain

// Sealed class for sorting and filtering options
sealed class FilterOption {
    data object Get : FilterOption() { val value = "GET" }
    data object Post : FilterOption() { val value = "POST" }
    data object Put : FilterOption() { val value = "PUT" }
    data object Delete : FilterOption() { val value = "DELETE" }
    data object Patch : FilterOption() { val value = "PATCH" }

    data object Success : FilterOption()
    data object Failure : FilterOption()

    data object NoFilter: FilterOption()
}