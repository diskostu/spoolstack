package de.diskostu.spoolstack.ui.filament.list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import de.diskostu.spoolstack.ui.components.DeleteConfirmationDialog
import de.diskostu.spoolstack.ui.theme.ActiveGreen
import de.diskostu.spoolstack.ui.theme.ActiveGreenDark
import de.diskostu.spoolstack.ui.theme.DeletedGray
import de.diskostu.spoolstack.ui.theme.DeletedGrayDark
import de.diskostu.spoolstack.ui.theme.SpoolstackTheme
import de.diskostu.spoolstack.ui.util.ColorUtils
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
    val filter by viewModel.filter.collectAsState()
    val sort by viewModel.sort.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
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
        filter = filter,
        sort = sort,
        sortOrder = sortOrder,
        searchQuery = searchQuery,
        onNavigateBack = onNavigateBack,
        onFilamentClick = onFilamentClick,
        onToggleDelete = { filament ->
            viewModel.toggleDeleted(filament)
        },
        onFilterChange = viewModel::setFilter,
        onSortChange = viewModel::setSort,
        onSearchQueryChange = viewModel::setSearchQuery
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentListContent(
    filaments: List<Filament>,
    filter: FilamentFilter,
    sort: FilamentSort,
    sortOrder: SortOrder,
    searchQuery: String,
    onNavigateBack: () -> Unit,
    onFilamentClick: (Int) -> Unit,
    onToggleDelete: (Filament) -> Unit,
    onFilterChange: (FilamentFilter) -> Unit,
    onSortChange: (FilamentSort) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    var filamentToDelete by remember { mutableStateOf<Filament?>(null) }

    // When search becomes active, request focus
    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

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
            Box(
                modifier = Modifier.height(72.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isSearchActive,
                    transitionSpec = {
                        if (targetState) {
                            // Search appears: Slide in from right, chips slide out to left
                            (slideInHorizontally { it } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it } + fadeOut())
                        } else {
                            // Search disappears: Slide out to right, chips slide in from left
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(
                                slideOutHorizontally { it } + fadeOut())
                        }
                    },
                    label = "search_animation"
                ) { active ->
                    if (active) {
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .focusRequester(focusRequester),
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    onSearchQueryChange("")
                                    isSearchActive = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.search_clear)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = filter == FilamentFilter.ALL,
                                    onClick = { onFilterChange(FilamentFilter.ALL) },
                                    label = { Text(stringResource(R.string.filter_all)) }
                                )
                                FilterChip(
                                    selected = filter == FilamentFilter.ACTIVE,
                                    onClick = { onFilterChange(FilamentFilter.ACTIVE) },
                                    label = { Text(stringResource(R.string.filter_active)) }
                                )
                                FilterChip(
                                    selected = filter == FilamentFilter.DELETED,
                                    onClick = { onFilterChange(FilamentFilter.DELETED) },
                                    label = { Text(stringResource(R.string.filter_deleted)) }
                                )
                            }

                            Box {
                                IconButton(onClick = { showSortMenu = true }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Sort,
                                        contentDescription = stringResource(R.string.sort_button_description)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    SortMenuItem(
                                        text = stringResource(R.string.sort_by_vendor),
                                        isSelected = sort == FilamentSort.VENDOR,
                                        sortOrder = sortOrder,
                                        onClick = {
                                            onSortChange(FilamentSort.VENDOR)
                                            showSortMenu = false
                                        }
                                    )
                                    SortMenuItem(
                                        text = stringResource(R.string.sort_by_color),
                                        isSelected = sort == FilamentSort.COLOR,
                                        sortOrder = sortOrder,
                                        onClick = {
                                            onSortChange(FilamentSort.COLOR)
                                            showSortMenu = false
                                        }
                                    )
                                    SortMenuItem(
                                        text = stringResource(R.string.sort_by_last_modified),
                                        isSelected = sort == FilamentSort.LAST_MODIFIED,
                                        sortOrder = sortOrder,
                                        onClick = {
                                            onSortChange(FilamentSort.LAST_MODIFIED)
                                            showSortMenu = false
                                        }
                                    )
                                    SortMenuItem(
                                        text = stringResource(R.string.sort_by_remaining_amount),
                                        isSelected = sort == FilamentSort.REMAINING_AMOUNT,
                                        sortOrder = sortOrder,
                                        onClick = {
                                            onSortChange(FilamentSort.REMAINING_AMOUNT)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }

                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.search_icon_description)
                                )
                            }
                        }
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
                        onToggleDelete = {
                            if (!filament.deleted && filament.currentWeight > 0) {
                                filamentToDelete = filament
                            } else {
                                onToggleDelete(filament)
                            }
                        },
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                    )
                }
            }
        }
    }

    filamentToDelete?.let { filament ->
        DeleteConfirmationDialog(
            onConfirm = {
                onToggleDelete(filament)
                filamentToDelete = null
            },
            onDismiss = { filamentToDelete = null },
            message = stringResource(R.string.delete_confirmation_message, filament.currentWeight)
        )
    }
}

@Composable
fun SortMenuItem(
    text: String,
    isSelected: Boolean,
    sortOrder: SortOrder,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
        trailingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = if (sortOrder == SortOrder.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null
                )
            }
        } else null,
        modifier = if (isSelected) {
            Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
        } else Modifier,
        colors = if (isSelected) {
            MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                trailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else MenuDefaults.itemColors()
    )
}

@Composable
fun FilamentCard(
    filament: Filament,
    onClick: () -> Unit,
    onToggleDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val backgroundColor = if (filament.deleted) {
        if (isDark) DeletedGrayDark else DeletedGray
    } else {
        ColorUtils.hexToColor(filament.colorHex) ?: if (isDark) ActiveGreenDark else ActiveGreen
    }

    val contentColor = if (filament.deleted) {
        MaterialTheme.colorScheme.onSurface
    } else {
        if (ColorUtils.isColorLight(backgroundColor)) Color.Black else Color.White
    }

    Card(
        modifier = modifier
            .testTag("filament_card")
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
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
                    text = stringResource(R.string.remaining_weight, "${filament.currentWeight}g"),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                    color = contentColor.copy(alpha = 0.8f)
                )

                if (!filament.boughtAt.isNullOrEmpty()) {
                    val purchaseInfo = if (filament.price != null && filament.price > 0) {
                        stringResource(
                            R.string.list_purchase_info_full,
                            filament.boughtAt,
                            filament.price
                        )
                    } else {
                        stringResource(R.string.list_purchase_info_vendor, filament.boughtAt)
                    }
                    Text(
                        text = purchaseInfo,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }

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
                    modifier = Modifier.padding(top = 4.dp),
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.menu_more_options),
                        tint = contentColor
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
                                    if (filament.deleted) R.string.undelete else R.string.delete
                                )
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleDelete()
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
@Preview(
    showBackground = true,
    heightDp = 500,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun FilamentListScreenPreview() {
    SpoolstackTheme {
        FilamentListContent(
            filaments = listOf(
                Filament(
                    id = 1,
                    vendor = "Prusament",
                    color = "Galaxy Black",
                    currentWeight = 1000,
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                ),
                Filament(
                    id = 2,
                    vendor = "Sunlu",
                    color = "PLA White",
                    currentWeight = 1000,
                    createdDate = System.currentTimeMillis(),
                    changeDate = System.currentTimeMillis()
                )
            ),
            filter = FilamentFilter.ALL,
            sort = FilamentSort.VENDOR,
            sortOrder = SortOrder.ASCENDING,
            searchQuery = "",
            onNavigateBack = {},
            onFilamentClick = {},
            onToggleDelete = {},
            onFilterChange = {},
            onSortChange = {},
            onSearchQueryChange = {}
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
                currentWeight = 1000,
                createdDate = System.currentTimeMillis(),
                changeDate = System.currentTimeMillis()
            ),
            onClick = {},
            onToggleDelete = {}
        )
    }
}
