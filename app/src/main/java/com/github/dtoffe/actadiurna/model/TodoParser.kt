package com.github.dtoffe.actadiurna.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TodoParser {
    private val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")
    private val contextRegex = Regex("""@(\S+)""")
    private val projectRegex = Regex("""\+(\S+)""")
    private val keyValueRegex = Regex("""(\b\w+):(\S+)""")

    fun parseLine(rawLine: String, id: Int): TodoItem {
        val trimmed = rawLine.trim()
        if (trimmed.isEmpty()) {
            return TodoItem(id = id, rawLine = rawLine, isCompleted = false, text = "")
        }

        var remaining = trimmed
        var isCompleted = false
        var completionDate: String? = null
        var creationDate: String? = null
        var priority: Char? = null

        // 1. Completion check: starts with 'x ' or 'X '
        if (remaining.startsWith("x ", ignoreCase = true) || remaining.startsWith("X ")) {
            isCompleted = true
            remaining = remaining.substring(2).trimStart()

            // Check completion date YYYY-MM-DD
            val parts = remaining.split(Regex("""\s+"""))
            var index = 0
            if (parts.isNotEmpty() && dateRegex.matches(parts[0])) {
                completionDate = parts[0]
                index++
                if (parts.size > index && dateRegex.matches(parts[index])) {
                    creationDate = parts[index]
                    index++
                }
            }
            remaining = parts.drop(index).joinToString(" ")
        } else {
            // 2. Priority check: starts with '(A) '
            if (remaining.length >= 4 &&
                remaining.startsWith("(") &&
                remaining[2] == ')' &&
                remaining[3] == ' ' &&
                remaining[1].isLetter()
            ) {
                priority = remaining[1].uppercaseChar()
                remaining = remaining.substring(4).trimStart()
            }

            // Check creation date YYYY-MM-DD
            val parts = remaining.split(Regex("""\s+"""))
            if (parts.isNotEmpty() && dateRegex.matches(parts[0])) {
                creationDate = parts[0]
                remaining = parts.drop(1).joinToString(" ")
            }
        }

        // 3. Extract contexts (@...), projects (+...), keyValues (key:val)
        val contexts = contextRegex.findAll(remaining).map { it.groupValues[1] }.distinct().toList()
        val projects = projectRegex.findAll(remaining).map { it.groupValues[1] }.distinct().toList()
        val keyValues = mutableMapOf<String, String>()
        keyValueRegex.findAll(remaining).forEach { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[2]
            keyValues[key] = value
        }

        // Check for pri: tag
        if (priority == null && keyValues.containsKey("pri")) {
            val pVal = keyValues["pri"]
            if (!pVal.isNullOrEmpty() && pVal[0].isLetter()) {
                priority = pVal[0].uppercaseChar()
            }
        }

        return TodoItem(
            id = id,
            rawLine = rawLine,
            isCompleted = isCompleted,
            priority = priority,
            completionDate = completionDate,
            creationDate = creationDate,
            text = remaining,
            contexts = contexts,
            projects = projects,
            keyValues = keyValues
        )
    }

    fun parseContent(content: String): List<TodoItem> {
        return content.lines()
            .mapIndexed { index, line -> parseLine(line, index) }
            .filter { it.rawLine.isNotBlank() || it.text.isNotBlank() }
    }

    fun generateRawContent(items: List<TodoItem>): String {
        return items.joinToString("\n") { it.toRawString() }
    }

    fun todayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
