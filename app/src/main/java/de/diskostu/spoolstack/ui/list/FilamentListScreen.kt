package de.diskostu.spoolstack.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import de.diskostu.spoolstack.R
import de.diskostu.spoolstack.data.Filament
import de.diskostu.spoolstack.ui.theme.ActiveGreen
import de.diskostu.spoolstack.ui.theme.ActiveGreenDark
import de.diskostu.spoolstack.ui.theme.ArchivedGray
import de.diskostu.spoolstack.ui.theme.ArchivedGrayDark
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale


@Composable
fun FilamentListScreen(
    onNavigateBack: () -> Unit,
    onFilamentClick: (Int) -> Unit,
    viewModel: FilamentListViewModel = hiltViewModel()
) {
    val filaments by viewModel.filaments.collectAsState()
    val totalFilaments by viewModel.totalFilaments.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onUiStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onUiStop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    FilamentListContent(
        filaments = filaments,
        totalFilaments = totalFilaments,
        filterState = filterState,
        searchQuery = searchQuery,
        onNavigateBack = onNavigateBack,
        onFilamentClick = onFilamentClick,
        onToggleArchive = { filament ->
            viewModel.toggleArchived(filament)
        },
        onFilterChange = { newFilterState ->
            viewModel.updateFilter(newFilterState)
        },
        onSearchQueryChange = { newQuery ->
            viewModel.updateSearchQuery(newQuery)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListContent(
    filaments: List<Filament>,
    totalFilaments: Int,
    filterState: FilterState,
    searchQuery: String,
    onNavigateBack: () -> Unit,
    onFilamentClick: (Int) -> Unit,
    onToggleArchive: (Filament) -> Unit,
    onFilterChange: (FilterState) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filament_list_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button_content_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (totalFilaments >= 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isSearchActive) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onClose = {
                                isSearchActive = false
                                onSearchQueryChange("")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        FilterChips(
                            filterState = filterState,
                            onFilterChange = onFilterChange,
                            onSearchClick = { isSearchActive = true }
                        )
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier
                    .testTag("filament_list")
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filaments,
                    key = { it.id }
                ) { filament ->
                    FilamentCard(
                        filament = filament,
                        onClick = { onFilamentClick(filament.id) },
                        onToggleArchive = { onToggleArchive(filament) },
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    onSearchClick: () -> Unit,
) {
    FilterChip(
        selected = filterState is FilterState.Archived,
        onClick = { onFilterChange(FilterState.Archived) },
        label = { Text(stringResource(R.string.filter_archived_label)) }
    )
    FilterChip(
        selected = filterState is FilterState.Active,
        onClick = { onFilterChange(FilterState.Active) },
        label = { Text(stringResource(R.string.filter_active_label)) }
    )
    FilterChip(
        selected = filterState is FilterState.All,
        onClick = { onFilterChange(FilterState.All) },
        label = { Text(stringResource(R.string.filter_all_label)) }
    )
    IconButton(onClick = onSearchClick) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(R.string.search_button_content_description)
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            singleLine = true
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.clear_search_button_content_description)
            )
        }
    }
}

@Composable
fun FilamentCard(
    filament: Filament,
    onClick: () -> Unit,
    onToggleArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (filament.archived) {
        if (isDark) ArchivedGrayDark else ArchivedGray
    } else {
        if (isDark) ActiveGreenDark else ActiveGreen
    }

    Card(
        modifier = modifier
            .testTag("filament_card")
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = filament.vendor + " | " + filament.color,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.remaining_weight, filament.size),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                val created =
                    stringResource(
                        R.string.created_date_label,
                        formatDate(filament.createdDate, df)
                    )
                val modified =
                    stringResource(R.string.changed_date_label, formatDate(filament.changeDate, df))
                Text(
                    text = "$created | $modified",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.menu_more_options)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit)) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    if (filament.archived) R.string.unarchive else R.string.archive
                                )
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleArchive()
                        }
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long, df: DateFormat): String {
    return df.format(Date(timestamp))
}

@Preview(showBackground = true, heightDp = 500, name = "Light Mode")
@Preview(showBackground = true, heightDp = 500, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilamentListScreenPreview() {
    SpoolstackTheme {
        FilamentListContent(
            filaments = listOf(
                Filament(
                    id = 1,
                    vendor = "Prusament",
                    color = "Galaxy Black",
                    size = "1kg",
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                ),
                Filament(
                    id = 2,
                    vendor = "Sunlu",
                    color = "PLA White",
                    size = "1kg",
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                )
            ),
            onNavigateBack = {},
            onFilamentClick = {},
            onToggleArchive = {}
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilamentCardPreview() {
    SpoolstackTheme {
        FilamentCard(
            filament = Filament(
                id = 1,
                vendor = "Prusament",
                color = "Galaxy Black",
                size = "1kg",
                createdDate = System.currentTimeMillis(),
                changeDate = System.currentTimeMillis()
            ),
            onClick = {},
            onToggleArchive = {}
        )
    }
}
