package com.github.dtoffe.actadiurna.model

import androidx.annotation.StringRes
import com.github.dtoffe.actadiurna.R

enum class StatusFilter(@StringRes val labelRes: Int) {
    ALL(R.string.filter_all),
    ACTIVE(R.string.filter_active),
    COMPLETED(R.string.filter_completed)
}

enum class SortBy(@StringRes val labelRes: Int) {
    PRIORITY(R.string.sort_priority),
    ALPHABETICAL(R.string.sort_alphabetical),
    PROJECT(R.string.sort_project),
    CONTEXT(R.string.sort_context),
    DUE_DATE(R.string.sort_due_date),
    CREATION_DATE(R.string.sort_creation_date),
    COMPLETION_DATE(R.string.sort_completion_date),
    LINE_ORDER(R.string.sort_line_order)
}
