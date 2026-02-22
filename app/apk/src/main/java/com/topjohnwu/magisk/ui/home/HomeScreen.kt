package com.topjohnwu.magisk.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.icon.extended.Layers
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Update
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import com.topjohnwu.magisk.core.R as CoreR
import com.topjohnwu.magisk.core.Info

/**
 * 主页面屏幕
 * 显示 Magisk 状态、管理器信息和开发者链接
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.surface,
        tint = HazeTint(MiuixTheme.colorScheme.surface.copy(0.8f))
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.hazeEffect(hazeState) {
                    style = hazeStyle
                    blurRadius = 30.dp
                    noiseFactor = 0f
                },
                color = Color.Transparent,
                title = context.getString(CoreR.string.section_home),
                scrollBehavior = scrollBehavior
            )
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .hazeSource(state = hazeState)
                .padding(horizontal = 12.dp),
            contentPadding = innerPadding,
            overscrollEffect = null
        ) {
            // 通知卡片
            if (viewModel.isNoticeVisible) {
                item {
                    NoticeCard(
                        onHide = { viewModel.hideNotice() }
                    )
                }
            }

            // Core 板块
            item {
                MagiskCard(
                    magiskState = viewModel.magiskState,
                    installedVersion = viewModel.magiskInstalledVersion.toString(),
                    onPressed = { viewModel.onMagiskPressed() }
                )
            }

            // 卸载按钮（红色警告色）
            if (Info.env.isActive) {
                item {
                    UninstallButton(
                        onPressed = { viewModel.onDeletePressed() }
                    )
                }
            }

            // Zygisk & Ramdisk 并排卡片
            item {
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ZygiskCard(
                        isEnabled = Info.isZygiskEnabled,
                        modifier = Modifier.weight(1f)
                    )
                    RamdiskCard(
                        isAvailable = Info.ramdisk,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // App 板块
            item {
                ManagerCard(
                    appState = viewModel.appState,
                    remoteVersion = viewModel.managerRemoteVersion.toString(),
                    installedVersion = viewModel.managerInstalledVersion,
                    packageName = context.packageName,
                    progress = viewModel.stateManagerProgress,
                    onPressed = { viewModel.onManagerPressed() }
                )
            }

            // 支持开发板块
            item {
                SupportCard(
                    onLinkPressed = { link ->
                        viewModel.onLinkPressed(link)
                    }
                )
            }

            // 贡献者板块
            item {
                FollowCard(
                    onLinkPressed = { link ->
                        viewModel.onLinkPressed(link)
                    }
                )
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * 通知卡片
 * 显示安全提示信息
 */
@Composable
private fun NoticeCard(
    onHide: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.primary,
            contentColor = MiuixTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(CoreR.string.home_notice_content),
                modifier = Modifier.weight(1f),
                color = MiuixTheme.colorScheme.onPrimary,
                style = MiuixTheme.textStyles.body1
            )
            TextButton(
                text = context.getString(CoreR.string.hide),
                onClick = onHide,
                colors = ButtonDefaults.textButtonColors(
                    textColor = MiuixTheme.colorScheme.onPrimary,
                    color = Color.Transparent
                )
            )
        }
    }
}

/**
 * Magisk 核心状态卡片
 * 显示 Magisk 版本、运行状态和操作按钮
 */
@Composable
private fun MagiskCard(
    magiskState: HomeViewModel.State,
    installedVersion: String,
    onPressed: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MiuixIcons.Download,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = context.getString(CoreR.string.magisk),
                        style = MiuixTheme.textStyles.title3,
                        color = MiuixTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusLabel(state = magiskState)
                }

                when (magiskState) {
                    HomeViewModel.State.OUTDATED -> {
                        Button(
                            onClick = onPressed,
                            colors = ButtonDefaults.buttonColorsPrimary()
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Update,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = context.getString(CoreR.string.update))
                        }
                    }
                    HomeViewModel.State.LOADING -> {
                        CircularProgressIndicator(
                            size = 24.dp,
                            strokeWidth = 2.dp
                        )
                    }
                    else -> {
                        Button(onClick = onPressed) {
                            Icon(
                                imageVector = MiuixIcons.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = context.getString(CoreR.string.install))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HomeItemRow(
                label = context.getString(CoreR.string.home_installed_version),
                value = installedVersion
            )
        }
    }
}

/**
 * 状态标签
 * 根据不同状态显示不同颜色的标签
 */
@Composable
private fun StatusLabel(state: HomeViewModel.State) {
    val context = LocalContext.current
    val (text, color) = when (state) {
        HomeViewModel.State.UP_TO_DATE -> context.getString(CoreR.string.home_latest_version) to MiuixTheme.colorScheme.primary
        HomeViewModel.State.OUTDATED -> context.getString(CoreR.string.update) to MiuixTheme.colorScheme.error
        HomeViewModel.State.INVALID -> context.getString(CoreR.string.not_available) to MiuixTheme.colorScheme.onSurfaceContainer
        HomeViewModel.State.LOADING -> context.getString(CoreR.string.loading) to MiuixTheme.colorScheme.onSurfaceContainer
    }
    Text(
        text = text,
        style = MiuixTheme.textStyles.footnote1,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

/**
 * Zygisk 状态卡片
 * 显示 Zygisk 启用/禁用状态
 */
@Composable
private fun ZygiskCard(
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statusColor = if (isEnabled) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceContainer

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = MiuixIcons.Settings,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = context.getString(CoreR.string.zygisk),
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEnabled) context.getString(CoreR.string.yes) else context.getString(CoreR.string.no),
                style = MiuixTheme.textStyles.footnote1,
                color = statusColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Ramdisk 状态卡片
 * 显示 Ramdisk 可用/不可用状态
 */
@Composable
private fun RamdiskCard(
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val statusColor = if (isAvailable) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurfaceContainer

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = MiuixIcons.Layers,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ramdisk",
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAvailable) context.getString(CoreR.string.yes) else context.getString(CoreR.string.no),
                style = MiuixTheme.textStyles.footnote1,
                color = statusColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 管理器卡片
 * 显示管理器应用版本和更新状态
 */
@Composable
private fun ManagerCard(
    appState: HomeViewModel.State,
    remoteVersion: String,
    installedVersion: String,
    packageName: String,
    progress: Int,
    onPressed: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MiuixIcons.Download,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = context.getString(CoreR.string.home_app_title),
                    style = MiuixTheme.textStyles.title3,
                    color = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                when (appState) {
                    HomeViewModel.State.OUTDATED -> {
                        Button(
                            onClick = onPressed,
                            colors = ButtonDefaults.buttonColorsPrimary()
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Update,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = context.getString(CoreR.string.update))
                        }
                    }
                    HomeViewModel.State.UP_TO_DATE -> {
                        Button(onClick = onPressed) {
                            Icon(
                                imageVector = MiuixIcons.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = context.getString(CoreR.string.install))
                        }
                    }
                    else -> {
                        CircularProgressIndicator(
                            size = 24.dp,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeItemRow(
                    label = context.getString(CoreR.string.home_latest_version),
                    value = remoteVersion
                )
                HomeItemRow(
                    label = context.getString(CoreR.string.home_installed_version),
                    value = installedVersion
                )
                HomeItemRow(
                    label = context.getString(CoreR.string.home_package),
                    value = packageName
                )
            }

            if (progress > 0 && progress < 100) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 卸载按钮
 * 用于卸载 Magisk
 */
@Composable
private fun UninstallButton(
    onPressed: () -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = onPressed,
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            color = MiuixTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = MiuixIcons.Delete,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = context.getString(CoreR.string.uninstall_magisk_title),
            style = MiuixTheme.textStyles.body2
        )
    }
}

/**
 * 支持卡片
 * 显示捐赠链接
 */
@Composable
private fun SupportCard(
    onLinkPressed: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = context.getString(CoreR.string.home_support_title),
                style = MiuixTheme.textStyles.title3
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = context.getString(CoreR.string.home_support_content),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconLinkItem(
                    onClick = { onLinkPressed(IconLink.Patreon.link) }
                )
                IconLinkItem(
                    onClick = { onLinkPressed(IconLink.PayPal.Project.link) }
                )
            }
        }
    }
}

/**
 * 关注卡片
 * 显示开发者链接
 */
@Composable
private fun FollowCard(
    onLinkPressed: (String) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = context.getString(CoreR.string.home_follow_title),
                style = MiuixTheme.textStyles.title3
            )

            Spacer(modifier = Modifier.height(8.dp))

            DeveloperItem(
                handle = DeveloperItem.John.handle,
                links = DeveloperItem.John.items,
                onLinkPressed = onLinkPressed
            )

            DeveloperItem(
                handle = DeveloperItem.Vvb.handle,
                links = DeveloperItem.Vvb.items,
                onLinkPressed = onLinkPressed
            )

            DeveloperItem(
                handle = DeveloperItem.YU.handle,
                links = DeveloperItem.YU.items,
                onLinkPressed = onLinkPressed
            )

            DeveloperItem(
                handle = DeveloperItem.Rikka.handle,
                links = DeveloperItem.Rikka.items,
                onLinkPressed = onLinkPressed
            )

            DeveloperItem(
                handle = DeveloperItem.Canyie.handle,
                links = DeveloperItem.Canyie.items,
                onLinkPressed = onLinkPressed
            )
        }
    }
}

/**
 * 主页信息行
 * 显示标签和值
 */
@Composable
private fun HomeItemRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.body2,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.body2,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
    }
}

/**
 * 图标链接项
 * 可点击的图标按钮
 */
@Composable
private fun IconLinkItem(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = MiuixIcons.Download,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * 开发者项
 * 显示开发者名称和社交链接
 */
@Composable
private fun DeveloperItem(
    handle: String,
    links: List<IconLink>,
    onLinkPressed: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = handle,
            style = MiuixTheme.textStyles.body2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            links.forEach { link ->
                IconButton(
                    onClick = { onLinkPressed(link.link) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
