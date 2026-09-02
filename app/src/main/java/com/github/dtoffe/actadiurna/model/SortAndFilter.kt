package com.github.dtoffe.actadiurna.model

enum class StatusFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}

enum class SortBy(val label: String) {
    PRIORITY("Priority"),
    ALPHABETICAL("A-Z"),
    PROJECT("Project"),
    CONTEXT("Context"),
    DUE_DATE("Due Date"),
    CREATION_DATE("Creation Date"),
    LINE_ORDER("File Order")
}
