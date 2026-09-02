package com.github.dtoffe.actadiurna.model

/**
 * Represents a single task item in todo.txt format.
 */
data class TodoItem(
    val id: Int,
    val rawLine: String = "",
    val isCompleted: Boolean = false,
    val priority: Char? = null,
    val completionDate: String? = null,
    val creationDate: String? = null,
    val text: String = "",
    val contexts: List<String> = emptyList(),
    val projects: List<String> = emptyList(),
    val keyValues: Map<String, String> = emptyMap()
) {
    val dueDate: String? get() = keyValues["due"]

    /**
     * Converts this task item back to standard todo.txt format line string.
     */
    fun toRawString(): String {
        val sb = StringBuilder()
        if (isCompleted) {
            sb.append("x ")
            if (!completionDate.isNullOrEmpty()) {
                sb.append(completionDate).append(" ")
            }
            if (!creationDate.isNullOrEmpty()) {
                sb.append(creationDate).append(" ")
            }
            // Preserve priority as pri:X tag if priority was set before completion
            var textWithPri = text
            if (priority != null && !keyValues.containsKey("pri") && !text.contains("pri:")) {
                textWithPri = "$text pri:$priority"
            }
            sb.append(textWithPri)
        } else {
            if (priority != null) {
                sb.append("(").append(priority.uppercaseChar()).append(") ")
            }
            if (!creationDate.isNullOrEmpty()) {
                sb.append(creationDate).append(" ")
            }
            sb.append(text)
        }
        return sb.toString().trim()
    }
}
