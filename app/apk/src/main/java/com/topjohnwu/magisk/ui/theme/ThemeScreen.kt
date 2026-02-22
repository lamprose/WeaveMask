package com.topjohnwu.magisk.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.topjohnwu.magisk.core.R as CoreR
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 主题选择页面
 * 使用 Compose 实现主题选择界面
 */
@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val themes = Theme.entries
    val selectedTheme = Theme.selected

    MiuixTheme {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = context.getString(CoreR.string.section_theme)
                )
            },
            contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        ThemeModeCard(
                            viewModel = viewModel
                        )
                    }

                    item {
                        ThemeSelectionCard(
                            themes = themes,
                            selectedTheme = selectedTheme,
                            onThemeSelected = { theme ->
                                viewModel.saveTheme(theme)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        )
    }
}

/**
 * 主题模式卡片
 * 显示当前主题模式设置
 */
@Composable
private fun ThemeModeCard(
    viewModel: ThemeViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { viewModel.onItemPressed(viewModel.themeHeadline) }
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
                    text = context.getString(CoreR.string.settings_dark_mode_title),
                    style = MiuixTheme.textStyles.body1,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = context.getString(CoreR.string.settings_dark_mode_message),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceContainer
                )
            }
        }
    }
}

/**
 * 主题选择卡片
 * 显示所有可选主题列表
 */
@Composable
private fun ThemeSelectionCard(
    themes: List<Theme>,
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit
) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = context.getString(CoreR.string.section_theme),
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.selectableGroup()) {
                themes.forEach { theme ->
                    ThemeItem(
                        theme = theme,
                        isSelected = theme == selectedTheme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        }
    }
}

/**
 * 单个主题选项
 */
@Composable
private fun ThemeItem(
    theme: Theme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = theme.themeName,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface
        )
    }
}
