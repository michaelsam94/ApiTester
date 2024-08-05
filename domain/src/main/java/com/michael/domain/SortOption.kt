package com.michael.domain

sealed class SortOption {
    data object Ascending : SortOption()
    data object Descending : SortOption()

    data object NoSorting: SortOption()

}