package com.github.dtoffe.actadiurna.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.dtoffe.actadiurna.data.TodoRepository
import com.github.dtoffe.actadiurna.model.SortBy
import com.github.dtoffe.actadiurna.model.StatusFilter
import com.github.dtoffe.actadiurna.model.TodoItem
import com.github.dtoffe.actadiurna.model.TodoParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

private data class FilterState(
    val query: String,
    val context: String?,
    val project: String?,
    val priority: Char?,
    val status: StatusFilter,
    val sort: SortBy,
)

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TodoRepository(application)

    val items: StateFlow<List<TodoItem>> = repository.items

    val searchQuery = MutableStateFlow("")
    val selectedContext = MutableStateFlow<String?>(null)
    val selectedProject = MutableStateFlow<String?>(null)
    val selectedPriority = MutableStateFlow<Char?>(null)
    val statusFilter = MutableStateFlow(StatusFilter.ALL)
    val sortBy = MutableStateFlow(SortBy.PRIORITY)

    val editingTask = MutableStateFlow<TodoItem?>(null)
    val selectedTask = MutableStateFlow<TodoItem?>(null)
    val snackbarMessage = MutableStateFlow<String?>(null)
    val showArchiveConfirmation = MutableStateFlow(false)

    // Extracted unique contexts from all tasks
    val allContexts: StateFlow<List<String>> = items.combine(selectedContext) { itemList, _ ->
        itemList.asSequence().flatMap { it.contexts }.distinct().sorted().toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Extracted unique projects from all tasks
    val allProjects: StateFlow<List<String>> = items.combine(selectedProject) { itemList, _ ->
        itemList.asSequence().flatMap { it.projects }.distinct().sorted().toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's due tasks count
    val dueTodayCount: StateFlow<Int> = items.map { itemList ->
        val today = TodoParser.todayDateString()
        itemList.count { (!it.isCompleted) && (it.dueDate == today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Overdue tasks count
    val overdueCount: StateFlow<Int> = items.map { itemList ->
        val today = TodoParser.todayDateString()
        itemList.count { (!it.isCompleted) && (it.dueDate != null) && (it.dueDate!! < today) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val filterParams = combine(
        combine(searchQuery, selectedContext, selectedProject) { q, ctx, prj -> Triple(q, ctx, prj) },
        combine(selectedPriority, statusFilter, sortBy) { pri, status, sort -> Triple(pri, status, sort) }
    ) { (q, ctx, prj), (pri, status, sort) ->
        FilterState(q, ctx, prj, pri, status, sort)
    }

    // Filtered and sorted tasks
    val filteredItems: StateFlow<List<TodoItem>> = combine(items, filterParams) { itemList, filter ->
        var list = itemList

        // 1. Status Filter
        list = when (filter.status) {
            StatusFilter.ACTIVE -> list.filter { !it.isCompleted }
            StatusFilter.COMPLETED -> list.filter { it.isCompleted }
            StatusFilter.ALL -> list
        }

        // 2. Context Filter
        if (filter.context != null) {
            list = list.filter { it.contexts.contains(filter.context) }
        }

        // 3. Project Filter
        if (filter.project != null) {
            list = list.filter { it.projects.contains(filter.project) }
        }

        // 4. Priority Filter
        if (filter.priority != null) {
            list = list.filter { it.priority == filter.priority }
        }

        // 5. Search Query Filter
        if (filter.query.isNotBlank()) {
            val q = filter.query.lowercase().trim()
            list = list.filter { item ->
                item.rawLine.lowercase().contains(q) ||
                        item.text.lowercase().contains(q) ||
                        item.contexts.any { it.lowercase().contains(q) } ||
                        item.projects.any { it.lowercase().contains(q) }
            }
        }

        // 6. Sorting
        when (filter.sort) {
            SortBy.PRIORITY -> list.sortedWith(
                compareBy<TodoItem> { it.isCompleted }
                    .thenBy { it.priority ?: ('Z' + 1) }
                    .thenBy { it.dueDate ?: "9999-99-99" }
                    .thenBy { it.id }
            )
            SortBy.ALPHABETICAL -> list.sortedWith(
                compareBy<TodoItem> { it.isCompleted }
                    .thenBy { it.text.lowercase() }
            )
            SortBy.PROJECT -> list.sortedWith(
                compareBy<TodoItem> { it.isCompleted }
                    .thenBy { it.projects.firstOrNull()?.lowercase() ?: "zzzzzz" }
                    .thenBy { it.priority ?: ('Z' + 1) }
                    .thenBy { it.text.lowercase() }
            )
            SortBy.CONTEXT -> list.sortedWith(
                compareBy<TodoItem> { it.isCompleted }
                    .thenBy { it.contexts.firstOrNull()?.lowercase() ?: "zzzzzz" }
                    .thenBy { it.priority ?: ('Z' + 1) }
                    .thenBy { it.text.lowercase() }
            )
            SortBy.DUE_DATE -> list.sortedWith(
                compareBy<TodoItem> { it.isCompleted }
                    .thenBy { it.dueDate ?: "9999-99-99" }
                    .thenBy { it.priority ?: ('Z' + 1) }
            )
            SortBy.CREATION_DATE -> list.sortedWith(
                compareByDescending<TodoItem> { it.creationDate ?: "" }
                    .thenBy { it.id }
            )
            SortBy.LINE_ORDER -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.loadInitialData()
        }
    }

    fun addRawTask(rawLine: String) {
        viewModelScope.launch {
            var finalLine = rawLine.trim()
            val today = TodoParser.todayDateString()

            // Add today's creation date if missing
            val hasPriority = finalLine.matches(Regex("""^\([A-Z]\)\s+.*"""))
            val hasDate = if (hasPriority) {
                finalLine.matches(Regex("""^\([A-Z]\)\s+\d{4}-\d{2}-\d{2}\b.*"""))
            } else {
                finalLine.matches(Regex("""^\d{4}-\d{2}-\d{2}\b.*"""))
            }

            if (!hasDate) {
                finalLine = if (hasPriority) {
                    val pri = finalLine.substring(0, 4)
                    val rest = finalLine.substring(4)
                    "$pri$today $rest"
                } else {
                    "$today $finalLine"
                }
            }

            repository.saveRawContent(
                if (repository.rawContent.value.isBlank()) finalLine 
                else "${repository.rawContent.value}\n$finalLine"
            )
            snackbarMessage.value = "Task added"
        }
    }

    fun toggleCompletion(item: TodoItem) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(item)
            snackbarMessage.value = if (!item.isCompleted) "Task completed" else "Task uncompleted"
        }
    }

    fun updatePriority(item: TodoItem, newPriority: Char?) {
        viewModelScope.launch {
            repository.updateTaskPriority(item, newPriority)
            snackbarMessage.value = if (newPriority != null) "Priority set to ($newPriority)" else "Priority cleared"
        }
    }

    fun updateTaskText(item: TodoItem, newRawLine: String) {
        viewModelScope.launch {
            var finalLine = newRawLine.trim()
            val today = TodoParser.todayDateString()
            
            // Add today's creation date if missing
            if (!finalLine.startsWith("x ", ignoreCase = true)) {
                val hasPriority = finalLine.matches(Regex("""^\([A-Z]\)\s+.*"""))
                val hasDate = if (hasPriority) {
                    finalLine.matches(Regex("""^\([A-Z]\)\s+\d{4}-\d{2}-\d{2}\b.*"""))
                } else {
                    finalLine.matches(Regex("""^\d{4}-\d{2}-\d{2}\b.*"""))
                }
                
                if (!hasDate) {
                    finalLine = if (hasPriority) {
                        val pri = finalLine.substring(0, 4)
                        val rest = finalLine.substring(4)
                        "$pri$today $rest"
                    } else {
                        "$today $finalLine"
                    }
                }
            }

            repository.updateTaskText(item, finalLine)
            editingTask.value = null
            if (selectedTask.value?.id == item.id) {
                // Refresh selection with updated item
                selectedTask.value = repository.items.value.find { it.id == item.id }
            }
            snackbarMessage.value = "Task updated"
        }
    }

    fun deleteTask(item: TodoItem) {
        viewModelScope.launch {
            repository.deleteTask(item)
            if (selectedTask.value?.id == item.id) {
                selectedTask.value = null
            }
            snackbarMessage.value = "Task deleted"
        }
    }

    fun saveRawContent(newContent: String) {
        viewModelScope.launch {
            repository.saveRawContent(newContent)
            snackbarMessage.value = "Tasks saved"
        }
    }

    fun archiveCompleted() {
        viewModelScope.launch {
            val count = repository.archiveCompletedTasks()
            snackbarMessage.value = if (count > 0) "Archived $count completed tasks to done.txt" else "No completed tasks to archive"
        }
    }

    fun importFromUri(uri: Uri) {
        viewModelScope.launch {
            val success = repository.importFromUri(uri)
            if (success) {
                snackbarMessage.value = "Successfully imported tasks"
            } else {
                snackbarMessage.value = "Failed to import file"
            }
        }
    }

    fun resetToSample() {
        viewModelScope.launch {
            repository.resetToSample()
            snackbarMessage.value = "Reset to sample tasks"
        }
    }

    fun toggleContext(item: TodoItem, contextName: String) {
        val tag = "@$contextName"
        val currentText = item.text
        val newText = if (item.contexts.contains(contextName)) {
            // Remove the tag precisely
            currentText.split(Regex("\\s+"))
                .filter { it != tag }
                .joinToString(" ")
        } else {
            if (currentText.isEmpty()) tag else "$currentText $tag"
        }
        val updatedItem = item.copy(text = newText)
        updateTaskText(item, updatedItem.toRawString())
    }

    fun toggleProject(item: TodoItem, projectName: String) {
        val tag = "+$projectName"
        val currentText = item.text
        val newText = if (item.projects.contains(projectName)) {
            // Remove the tag precisely
            currentText.split(Regex("\\s+"))
                .filter { it != tag }
                .joinToString(" ")
        } else {
            if (currentText.isEmpty()) tag else "$currentText $tag"
        }
        val updatedItem = item.copy(text = newText)
        updateTaskText(item, updatedItem.toRawString())
    }

    fun createShareIntent(): Intent {
        val app = getApplication<Application>()
        val file = File(app.filesDir, "todo.txt")
        val uri = FileProvider.getUriForFile(
            app,
            "${app.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "todo.txt")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(intent, "Share todo.txt file via")
    }

    fun dismissSnackbar() {
        snackbarMessage.value = null
    }
}
