package com.github.dtoffe.actadiurna.data

import android.content.Context
import android.net.Uri
import com.github.dtoffe.actadiurna.model.TodoItem
import com.github.dtoffe.actadiurna.model.TodoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStreamReader

class TodoRepository(private val context: Context) {

    private val todoFile = File(context.filesDir, "todo.txt")
    private val doneFile = File(context.filesDir, "done.txt")

    private val _rawContent = MutableStateFlow("")
    val rawContent: StateFlow<String> = _rawContent.asStateFlow()

    private val _items = MutableStateFlow<List<TodoItem>>(emptyList())
    val items: StateFlow<List<TodoItem>> = _items.asStateFlow()

    private val defaultSampleContent = """
(A) 2026-08-08 Welcome to todo.txt! @app +tutorial due:2026-08-17
(A) 2026-08-17 Review important project tasks +work @office
(B) 2026-08-15 Buy coffee beans @groceries +home
(C) 2026-08-14 Call plumber for kitchen sink @phone +home
x 2026-08-17 2026-08-17 Completed setup task @app
2026-08-16 Organize workspace desk @home
    """.trimIndent()

    suspend fun loadInitialData() = withContext(Dispatchers.IO) {
        if (!todoFile.exists()) {
            todoFile.writeText(defaultSampleContent)
        }
        val content = todoFile.readText()
        updateContentState(content)
    }

    suspend fun saveRawContent(newContent: String) = withContext(Dispatchers.IO) {
        todoFile.writeText(newContent)
        updateContentState(newContent)
    }

    private fun updateContentState(content: String) {
        _rawContent.value = content
        val parsed = TodoParser.parseContent(content)
        _items.value = parsed
    }

    suspend fun toggleTaskCompletion(item: TodoItem) = withContext(Dispatchers.IO) {
        val today = TodoParser.todayDateString()
        val updatedItem = if (item.isCompleted) {
            // Un-complete: remove completion status and completion date
            item.copy(
                isCompleted = false,
                completionDate = null
            )
        } else {
            // Complete: set complete, completion date = today
            item.copy(
                isCompleted = true,
                completionDate = today,
                creationDate = item.creationDate ?: today
            )
        }

        updateSingleItem(updatedItem)
    }

    suspend fun updateTaskPriority(item: TodoItem, newPriority: Char?) = withContext(Dispatchers.IO) {
        val updatedItem = item.copy(priority = newPriority)
        updateSingleItem(updatedItem)
    }

    suspend fun updateTaskText(item: TodoItem, newRawLine: String) = withContext(Dispatchers.IO) {
        val reParsed = TodoParser.parseLine(newRawLine, item.id)
        updateSingleItem(reParsed)
    }

    suspend fun deleteTask(item: TodoItem) = withContext(Dispatchers.IO) {
        val currentItems = _items.value.toMutableList()
        currentItems.removeAll { it.id == item.id }
        val newRaw = TodoParser.generateRawContent(currentItems)
        saveRawContent(newRaw)
    }

    private suspend fun updateSingleItem(newItem: TodoItem) {
        val currentItems = _items.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == newItem.id }
        if (index != -1) {
            currentItems[index] = newItem
            val newRaw = TodoParser.generateRawContent(currentItems)
            saveRawContent(newRaw)
        }
    }

    suspend fun archiveCompletedTasks(): Int = withContext(Dispatchers.IO) {
        val allItems = _items.value
        val (completed, active) = allItems.partition { it.isCompleted }
        if (completed.isEmpty()) return@withContext 0

        // Append to done.txt
        val completedRaw = TodoParser.generateRawContent(completed)
        if (doneFile.exists()) {
            doneFile.appendText("\n" + completedRaw)
        } else {
            doneFile.writeText(completedRaw)
        }

        // Save remaining active tasks
        val activeRaw = TodoParser.generateRawContent(active)
        saveRawContent(activeRaw)

        completed.size
    }

    suspend fun importFromUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val content = reader.readText()
                    saveRawContent(content)
                    return@withContext true
                }
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun resetToSample() = withContext(Dispatchers.IO) {
        saveRawContent(defaultSampleContent)
    }
}
