package com.github.dtoffe.actadiurna.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.dtoffe.actadiurna.model.SortBy
import com.github.dtoffe.actadiurna.ui.components.TaskItemCard
import com.github.dtoffe.actadiurna.ui.theme.TodoIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScreen(
    viewModel: TodoViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.filteredDoneItems.collectAsState()
    val searchQuery by viewModel.doneSearchQuery.collectAsState()
    val sortBy by viewModel.doneSortBy.collectAsState()
    val selectedIds by viewModel.selectedDoneTasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Archived Tasks (done.txt)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = { viewModel.unarchiveSelectedTasks() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Unarchive selected", tint = Color(0xFF388E3C))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            // 1. Search Bar (Same slim style)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.doneSearchQuery.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            interactionSource = interactionSource,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                "Search archived...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        innerTextField()
                                    }
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.doneSearchQuery.value = "" },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Clear search",
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }

                    // 2. Sort Buttons Area (Centered)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sortOptions = listOf(
                            SortBy.COMPLETION_DATE to Icons.Default.Refresh, // Changed to Refresh since History is missing
                            SortBy.ALPHABETICAL to TodoIcons.SortAlpha,
                            SortBy.PRIORITY to Icons.Default.Star,
                            SortBy.PROJECT to TodoIcons.Project,
                            SortBy.CONTEXT to TodoIcons.Context
                        )

                        sortOptions.forEach { (option, icon) ->
                            val isSelected = sortBy == option
                            FilterChip(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                selected = isSelected,
                                onClick = { viewModel.doneSortBy.value = option },
                                label = {
                                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = option.label,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                                   else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // 3. Task List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No archived tasks match your search" else "Archive is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items, key = { it.id }) { item ->
                            TaskItemCard(
                                item = item,
                                onToggleCompletion = { /* Not allowed here */ },
                                onUpdatePriority = { /* Not allowed here */ },
                                onEdit = { /* Not allowed here */ },
                                onDelete = { /* Not allowed here */ },
                                sortBy = sortBy,
                                isSelected = item.id in selectedIds,
                                onSelect = {
                                    viewModel.toggleDoneTaskSelection(item.id)
                                }
                            )
                        }
                    }
                }
            }
            
            if (selectedIds.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${selectedIds.size} selected", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { viewModel.unarchiveSelectedTasks() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Unarchive")
                        }
                    }
                }
            }
        }
    }
}
