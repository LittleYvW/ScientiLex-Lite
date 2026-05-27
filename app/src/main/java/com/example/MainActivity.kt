package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.ScrollState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.DarkAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkContainer
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LightAccent
import com.example.ui.theme.LightBg
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightContainer
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AcademiaRoot()
    }
  }
}

enum class PanelType {
  CENTER, ABOUT, SETTINGS, CATALOG, MANAGER
}

class AcademiaViewModel : ViewModel() {
  private val _uiThemeMode = MutableStateFlow("light")
  val uiThemeMode: StateFlow<String> = _uiThemeMode.asStateFlow()

  private val _fontScaleMode = MutableStateFlow("normal")
  val fontScaleMode: StateFlow<String> = _fontScaleMode.asStateFlow()

  private val _currentPanel = MutableStateFlow(PanelType.CENTER)
  val currentPanel: StateFlow<PanelType> = _currentPanel.asStateFlow()

  private val _searchTerm = MutableStateFlow("")
  val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

  private val _catalogCategory = MutableStateFlow("all")
  val catalogCategory: StateFlow<String> = _catalogCategory.asStateFlow()

  // Reader state
  private val _isReaderOpen = MutableStateFlow(false)
  val isReaderOpen: StateFlow<Boolean> = _isReaderOpen.asStateFlow()

  private val _activeReaderDocId = MutableStateFlow("hci")
  val activeReaderDocId: StateFlow<String> = _activeReaderDocId.asStateFlow()

  private val _readerPage = MutableStateFlow(1)
  val readerPage: StateFlow<Int> = _readerPage.asStateFlow()

  private val _readerLanguage = MutableStateFlow("ZH")
  val readerLanguage: StateFlow<String> = _readerLanguage.asStateFlow()

  private val _readerHUDVisible = MutableStateFlow(true)
  val readerHUDVisible: StateFlow<Boolean> = _readerHUDVisible.asStateFlow()

  private val _localDocsCount = MutableStateFlow(2)
  val localDocsCount: StateFlow<Int> = _localDocsCount.asStateFlow()

  // Action flow for toast alerts
  val toastSharedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

  fun setThemeMode(mode: String) {
    _uiThemeMode.value = mode
    showToast("视觉主题已更改")
  }

  fun setFontScaleMode(mode: String) {
    _fontScaleMode.value = mode
    showToast("排版比例已更改")
  }

  fun setCurrentPanel(panel: PanelType) {
    _currentPanel.value = panel
  }

  fun setSearchTerm(term: String) {
    _searchTerm.value = term
  }

  fun setCatalogCategory(category: String) {
    _catalogCategory.value = category
  }

  fun openReader(docId: String) {
    _activeReaderDocId.value = docId
    _readerPage.value = 1
    _readerLanguage.value = "ZH"
    _readerHUDVisible.value = true
    _isReaderOpen.value = true
  }

  fun closeReader() {
    _isReaderOpen.value = false
    _readerHUDVisible.value = false
  }

  fun setReaderPage(page: Int) {
    _readerPage.value = page
  }

  fun toggleReaderLanguage() {
    val nextLang = if (_readerLanguage.value == "ZH") "EN" else "ZH"
    _readerLanguage.value = nextLang
    showToast(if (nextLang == "ZH") "已翻译：学术中文" else "Translate to: English")
  }

  fun toggleReaderHUD() {
    _readerHUDVisible.value = !_readerHUDVisible.value
  }

  fun incrementDocs() {
    _localDocsCount.value += 1
    showToast("本地文献成功导入")
  }

  fun showToast(message: String) {
    toastSharedFlow.tryEmit(message)
  }
}

@Composable
fun AcademiaRoot(viewModel: AcademiaViewModel = viewModel()) {
  val themeMode by viewModel.uiThemeMode.collectAsState()
  val darkTheme = themeMode == "dark"

  MyApplicationTheme(darkTheme = darkTheme) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      AcademiaApp(viewModel)
    }
  }
}

@Composable
fun AcademiaApp(viewModel: AcademiaViewModel) {
  val currentPanel by viewModel.currentPanel.collectAsState()
  val isReaderOpen by viewModel.isReaderOpen.collectAsState()
  val localDocsCount by viewModel.localDocsCount.collectAsState()
  val searchTerm by viewModel.searchTerm.collectAsState()
  val fontScaleMode by viewModel.fontScaleMode.collectAsState()

  // Typography scalar based on Settings
  val fontScale = if (fontScaleMode == "compact") 0.85f else 1.0f

  // Scroll states for panels to control gesture enabled states smoothly
  val catalogScrollState = rememberScrollState()
  val managementScrollState = rememberScrollState()
  val density = LocalDensity.current

  // Standard toast notification overlay overlayed on active layers
  var activeToastText by remember { mutableStateOf<String?>(null) }
  LaunchedEffect(Unit) {
    viewModel.toastSharedFlow.collect { msg ->
      activeToastText = msg
    }
  }

  // Toast auto-fade out
  LaunchedEffect(activeToastText) {
    if (activeToastText != null) {
      delay(2000)
      activeToastText = null
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val W = maxWidth
    val H = maxHeight

    // Main 2D scroll grid coordinates
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }
    var dragAxis by remember { mutableStateOf<String?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    val threshold = remember(W, H) { calculateThreshold(W.value, H.value) }

    // Sync resting coordinate positions
    LaunchedEffect(currentPanel, W, H) {
      val targetX = when (currentPanel) {
        PanelType.CENTER -> 0f
        PanelType.ABOUT -> W.value
        PanelType.SETTINGS -> -W.value
        else -> 0f
      }
      val targetY = when (currentPanel) {
        PanelType.CENTER -> 0f
        PanelType.CATALOG -> H.value
        PanelType.MANAGER -> -H.value
        else -> 0f
      }
      offsetX.animateTo(targetX, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
      offsetY.animateTo(targetY, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
    }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Touch gesture capture zone
    val gestureModifier = if (!isReaderOpen) {
      Modifier.pointerInput(currentPanel, W, H) {
        detectDragGestures(
          onDragStart = {
            dragX = 0f
            dragY = 0f
            dragAxis = null
            isDragging = true
            focusManager.clearFocus()
          },
          onDrag = { change, dragAmount ->
            change.consume()
            val dxDp = with(density) { dragAmount.x.toDp().value }
            val dyDp = with(density) { dragAmount.y.toDp().value }
            dragX += dxDp
            dragY += dyDp

            if (dragAxis == null) {
              if (abs(dragX) > 15f || abs(dragY) > 15f) {
                dragAxis = if (abs(dragX) > abs(dragY)) "x" else "y"
              }
            }

            // Real-time damping updates
            coroutineScope.launch {
              val wPx = W.value
              val hPx = H.value
              val baseOffsetX = when (currentPanel) {
                PanelType.ABOUT -> wPx
                PanelType.SETTINGS -> -wPx
                else -> 0f
              }
              val baseOffsetY = when (currentPanel) {
                PanelType.CATALOG -> hPx
                PanelType.MANAGER -> -hPx
                else -> 0f
              }

              val dampX = applyHighResistance(dragX)
              val dampY = applyHighResistance(dragY)

              var tx = baseOffsetX
              var ty = baseOffsetY

              if (currentPanel == PanelType.CENTER) {
                if (dragAxis == "x") {
                  tx = baseOffsetX + dampX
                  ty = 0f
                } else if (dragAxis == "y") {
                  tx = 0f
                  ty = baseOffsetY + dampY
                }
              } else {
                if (currentPanel == PanelType.ABOUT && dragX < 0f && dragAxis == "x") {
                  tx = baseOffsetX + dampX
                } else if (currentPanel == PanelType.SETTINGS && dragX > 0f && dragAxis == "x") {
                  tx = baseOffsetX + dampX
                } else if (currentPanel == PanelType.CATALOG && dragY < 0f && dragAxis == "y") {
                  ty = baseOffsetY + dampY
                } else if (currentPanel == PanelType.MANAGER && dragY > 0f && dragAxis == "y") {
                  ty = baseOffsetY + dampY
                } else {
                  tx = baseOffsetX + applyHighResistance(dragX) * 0.15f
                  ty = baseOffsetY + applyHighResistance(dragY) * 0.15f
                }
              }

              offsetX.snapTo(tx)
              offsetY.snapTo(ty)
            }
          },
          onDragEnd = {
            isDragging = false
            coroutineScope.launch {
              var targetPanel = currentPanel
              if (currentPanel == PanelType.CENTER) {
                if (dragAxis == "x") {
                  if (dragX >= threshold) targetPanel = PanelType.ABOUT
                  else if (dragX <= -threshold) targetPanel = PanelType.SETTINGS
                } else if (dragAxis == "y") {
                  if (dragY >= threshold) targetPanel = PanelType.CATALOG
                  else if (dragY <= -threshold) targetPanel = PanelType.MANAGER
                }
              } else {
                if (currentPanel == PanelType.ABOUT && dragX <= -threshold) targetPanel = PanelType.CENTER
                else if (currentPanel == PanelType.SETTINGS && dragX >= threshold) targetPanel = PanelType.CENTER
                else if (currentPanel == PanelType.CATALOG && dragY <= -threshold) targetPanel = PanelType.CENTER
                else if (currentPanel == PanelType.MANAGER && dragY >= threshold) targetPanel = PanelType.CENTER
              }
              if (targetPanel == currentPanel) {
                val targetX = when (currentPanel) {
                  PanelType.CENTER -> 0f
                  PanelType.ABOUT -> W.value
                  PanelType.SETTINGS -> -W.value
                  else -> 0f
                }
                val targetY = when (currentPanel) {
                  PanelType.CENTER -> 0f
                  PanelType.CATALOG -> H.value
                  PanelType.MANAGER -> -H.value
                  else -> 0f
                }
                coroutineScope.launch {
                  offsetX.animateTo(targetX, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
                }
                coroutineScope.launch {
                  offsetY.animateTo(targetY, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
                }
              } else {
                viewModel.setCurrentPanel(targetPanel)
              }
            }
          },
          onDragCancel = {
            isDragging = false
            coroutineScope.launch {
              val targetX = when (currentPanel) {
                PanelType.CENTER -> 0f
                PanelType.ABOUT -> W.value
                PanelType.SETTINGS -> -W.value
                else -> 0f
              }
              val targetY = when (currentPanel) {
                PanelType.CENTER -> 0f
                PanelType.CATALOG -> H.value
                PanelType.MANAGER -> -H.value
                else -> 0f
              }
              coroutineScope.launch {
                offsetX.animateTo(targetX, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
              }
              coroutineScope.launch {
                offsetY.animateTo(targetY, animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
              }
            }
          }
        )
      }
    } else {
      Modifier
    }

    // 2D grid structural panels renderer
    Box(
      modifier = Modifier
        .fillMaxSize()
        .then(gestureModifier)
    ) {
      val currentX = offsetX.value.dp
      val currentY = offsetY.value.dp

      // Left Panel: About / Manifesto
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset((currentX - W).roundToPx(), currentY.roundToPx()) }
      ) {
        AboutPanel(fontScale, currentPanel == PanelType.ABOUT)
      }

      // Right Panel: Settings / Preferences
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset((currentX + W).roundToPx(), currentY.roundToPx()) }
      ) {
        SettingsPanel(viewModel, fontScale, currentPanel == PanelType.SETTINGS)
      }

      // Top Panel: Catalog / Archive
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset(currentX.roundToPx(), (currentY - H).roundToPx()) }
      ) {
        CatalogPanel(viewModel, fontScale, currentPanel == PanelType.CATALOG, catalogScrollState)
      }

      // Bottom Panel: Management / Architecture
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset(currentX.roundToPx(), (currentY + H).roundToPx()) }
      ) {
        ManagementPanel(viewModel, localDocsCount, fontScale, currentPanel == PanelType.MANAGER, managementScrollState)
      }

      // Center Panel: Search Dashboard
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset(currentX.roundToPx(), currentY.roundToPx()) }
      ) {
        SearchPanel(viewModel, fontScale, currentPanel == PanelType.CENTER)
      }
    }

    // Dynamic Edge drag HUD indicators inside center grid navigation
    if (!isReaderOpen) {
      ActiveNavigationIndicators(
        currentPanel = currentPanel,
        isDragging = isDragging,
        dragAxis = dragAxis,
        dragX = dragX,
        dragY = dragY,
        threshold = threshold
      )
    }

    // Overlay full-bleed Reader View Screen
    AnimatedVisibility(
      visible = isReaderOpen,
      enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
              slideInVertically(initialOffsetY = { it / 14 }, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
      exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
              slideOutVertically(targetOffsetY = { it / 14 }, animationSpec = spring(stiffness = Spring.StiffnessMedium))
    ) {
      ReaderScreen(viewModel, fontScale)
    }

    // Custom Toast notifications
    AnimatedVisibility(
      visible = activeToastText != null,
      enter = fadeIn(animationSpec = spring()) + slideInVertically(initialOffsetY = { 30 }),
      exit = fadeOut(animationSpec = spring()) + slideOutVertically(targetOffsetY = { 30 }),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 64.dp)
        .widthIn(max = 300.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(MaterialTheme.colorScheme.onBackground)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Text(
          text = activeToastText ?: "",
          color = MaterialTheme.colorScheme.background,
          style = TextStyle(
            fontSize = 13.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.05.sp,
            textAlign = TextAlign.Center
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

// -------------------------------------------------------------
// Interactive Paneling Views
// -------------------------------------------------------------

@Composable
fun PanelHeader(subtitle: String, title: String, fontScale: Float) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 24.dp)
  ) {
    Text(
      text = subtitle,
      color = MaterialTheme.colorScheme.secondary,
      style = TextStyle(
        fontSize = (11.sp * fontScale),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.15.sp
      )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      color = MaterialTheme.colorScheme.onBackground,
      style = TextStyle(
        fontSize = (26.sp * fontScale),
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
fun AboutPanel(fontScale: Float, isActive: Boolean) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp)
      .padding(horizontal = 32.dp)
  ) {
    PanelHeader("MANIFESTO", "设计理念与溯源", fontScale)

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(vertical = 12.dp)
    ) {
      Text(
        text = "“学术是一场克制的长跑，而工具本身不应成为干扰。”",
        color = MaterialTheme.colorScheme.onBackground,
        style = TextStyle(
          fontSize = (17.sp * fontScale),
          fontFamily = FontFamily.Serif,
          fontStyle = FontStyle.Italic,
          lineHeight = 30.sp,
          fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(bottom = 24.dp)
      )

      Text(
        text = "    Academia 尝试回答一个关于“信息熵”的命题：如何在不牺牲检索效率的前提下，消解现代应用中多余的视觉噪音与控制逻辑？我们摒弃了传统的页面跳转，转而采用纯粹的物理空间手势来映射界面的运作形态。",
        color = MaterialTheme.colorScheme.onBackground,
        style = TextStyle(
          fontSize = (15.sp * fontScale),
          fontFamily = FontFamily.Serif,
          lineHeight = 28.sp
        ),
        modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
        text = "    左侧面板为关于系统，右侧为设置，上方为归档目录，下方为内容管理。拖拽时画布通过强阻尼阻断了其他面板的泄露，仅在边缘进行呼吸式指示反馈，力求实现低熵、专注的学术工作空间。",
        color = MaterialTheme.colorScheme.onBackground,
        style = TextStyle(
          fontSize = (15.sp * fontScale),
          fontFamily = FontFamily.Serif,
          lineHeight = 28.sp
        ),
        modifier = Modifier.padding(bottom = 16.dp)
      )
    }
  }
}

@Composable
fun SettingsPanel(viewModel: AcademiaViewModel, fontScale: Float, isActive: Boolean) {
  val currentTheme by viewModel.uiThemeMode.collectAsState()
  val currentDensity by viewModel.fontScaleMode.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp)
      .padding(horizontal = 32.dp)
  ) {
    PanelHeader("PREFERENCES", "秩序与偏好设置", fontScale)

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(top = 16.dp),
      verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
      // Preference Row 1: UI Theme Selector
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "阅读模式主题 / UI Palette",
          color = MaterialTheme.colorScheme.onBackground,
          style = TextStyle(
            fontSize = (14.sp * fontScale),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold
          ),
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
          modifier = Modifier
            .background(
              color = if (currentTheme == "dark") DarkBorder else LightBorder,
              shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (currentTheme == "light") MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { viewModel.setThemeMode("light") }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "象牙白",
              color = if (currentTheme == "light") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
              style = TextStyle(fontSize = (13.sp * fontScale), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (currentTheme == "dark") MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { viewModel.setThemeMode("dark") }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "墨池黑",
              color = if (currentTheme == "dark") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
              style = TextStyle(fontSize = (13.sp * fontScale), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
)
          }
        }
      }

      // Preference Row 2: Text Scaling density
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "文字排版尺度 / Text Density",
          color = MaterialTheme.colorScheme.onBackground,
          style = TextStyle(
            fontSize = (14.sp * fontScale),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold
          ),
          modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
          modifier = Modifier
            .background(
              color = if (currentTheme == "dark") DarkBorder else LightBorder,
              shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (currentDensity == "normal") MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { viewModel.setFontScaleMode("normal") }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "标准",
              color = if (currentDensity == "normal") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
              style = TextStyle(fontSize = (13.sp * fontScale), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (currentDensity == "compact") MaterialTheme.colorScheme.surface else Color.Transparent)
              .clickable { viewModel.setFontScaleMode("compact") }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "紧凑",
              color = if (currentDensity == "compact") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary,
              style = TextStyle(fontSize = (13.sp * fontScale), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogPanel(viewModel: AcademiaViewModel, fontScale: Float, isActive: Boolean, scrollState: ScrollState) {
  val searchTerm by viewModel.searchTerm.collectAsState()
  val selectedCat by viewModel.catalogCategory.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp)
      .padding(horizontal = 32.dp)
  ) {
    PanelHeader("ARCHIVE", "文献归档目录", fontScale)

    // Category Chips list
    FlowRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      CategoryChip("全部领域", selectedCat == "all", fontScale) { viewModel.setCatalogCategory("all") }
      CategoryChip("人机交互", selectedCat == "hci", fontScale) { viewModel.setCatalogCategory("hci") }
      CategoryChip("认知心理学", selectedCat == "cog", fontScale) { viewModel.setCatalogCategory("cog") }
    }

    // Dynamic filtered catalog articles list
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(
          state = scrollState,
          enabled = scrollState.value > 0
        )
        .padding(bottom = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      val papers = listOf(
        CatalogPaperItem(
          id = "hci",
          category = "hci",
          meta = "2023 · HCI PERSPECTIVE · 点击阅读",
          title = "Information Entropy and Minimalist User Interfaces",
          desc = "本研究旨在降低学术检索系统中的认知负荷。我们探讨了交互手势如何减少对传统控制控件的依赖，使阅读重回文本深度体验。"
        ),
        CatalogPaperItem(
          id = "cog",
          category = "cog",
          meta = "2024 · COGNITION ACADEMY · 点击阅读",
          title = "Constructing Conceptual Space in High-Dimensional Search",
          desc = "对人脑构建学术知识关系时的空间物理映射进行认知分析，支持高维特征在平面交互空间中的投影表征。"
        )
      )

      val filtered = papers.filter {
        val matchCat = (selectedCat == "all" || it.category == selectedCat)
        val matchSearch = (searchTerm.isEmpty() ||
            it.title.contains(searchTerm, ignoreCase = true) ||
            it.desc.contains(searchTerm, ignoreCase = true))
        matchCat && matchSearch
      }

      if (filtered.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "未检索到匹配的文献 ──\n可尝试清理过滤词后重新浏览",
            color = MaterialTheme.colorScheme.secondary,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 22.sp,
              textAlign = TextAlign.Center
            )
          )
        }
      } else {
        filtered.forEach { paper ->
          CatalogArticleItem(paper, fontScale) {
            viewModel.openReader(paper.id)
          }
        }
      }
    }
  }
}

data class CatalogPaperItem(
  val id: String,
  val category: String,
  val meta: String,
  val title: String,
  val desc: String
)

@Composable
fun CategoryChip(text: String, isSelected: Boolean, fontScale: Float, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
      .border(
        width = 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        shape = RoundedCornerShape(20.dp)
      )
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Text(
      text = text,
      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary,
      style = TextStyle(
        fontSize = (12.sp * fontScale),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
fun CatalogArticleItem(paper: CatalogPaperItem, fontScale: Float, onClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 12.dp)
  ) {
    Text(
      text = paper.meta,
      color = MaterialTheme.colorScheme.secondary,
      style = TextStyle(
        fontSize = (11.sp * fontScale),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.05.sp
      )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = paper.title,
      color = MaterialTheme.colorScheme.onBackground,
      style = TextStyle(
        fontSize = (18.sp * fontScale),
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp
      )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = paper.desc,
      color = MaterialTheme.colorScheme.secondary,
      style = TextStyle(
        fontSize = (13.sp * fontScale),
        fontFamily = FontFamily.SansSerif,
        lineHeight = 20.sp
      )
    )
    Spacer(modifier = Modifier.height(16.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.outline)
    )
  }
}

@Composable
fun ManagementPanel(viewModel: AcademiaViewModel, docsCount: Int, fontScale: Float, isActive: Boolean, scrollState: ScrollState) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp)
      .padding(horizontal = 32.dp)
  ) {
    PanelHeader("ARCHITECTURE", "本地存储与导入控制", fontScale)

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(
          state = scrollState,
          enabled = scrollState.value > 0
        )
    ) {
      // Plus drop zone button mockup
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
          .drawBehind {
            // Simulated dotted drawing or custom thin outer borders
            val bColor = if (docsCount > 2) LightAccent.copy(alpha = 0.3f) else LightBorder
            val strokeWidth = 1.dp.toPx()
            drawRect(
              color = bColor,
              style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
              )
            )
          }
          .clickable { viewModel.incrementDocs() }
          .padding(vertical = 40.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterAlignmentLineMap() ?: Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "导入文献",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "点击此区域模拟解析并导入一份文献 (PDF/JSON)",
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "或拖动任意学术描述性文件至此区域",
            color = MaterialTheme.colorScheme.secondary,
            style = TextStyle(
              fontSize = (12.sp * fontScale),
              fontFamily = FontFamily.SansSerif
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Multi stats cards grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = borderStroke(MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "$docsCount",
              color = MaterialTheme.colorScheme.primary,
              style = TextStyle(
                fontSize = (26.sp * fontScale),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "本地知识库条目",
              color = MaterialTheme.colorScheme.secondary,
              style = TextStyle(
                fontSize = (12.sp * fontScale),
                fontFamily = FontFamily.SansSerif
              )
            )
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = borderStroke(MaterialTheme.colorScheme.outline)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "4,216",
              color = MaterialTheme.colorScheme.primary,
              style = TextStyle(
                fontSize = (26.sp * fontScale),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "关联网络路径",
              color = MaterialTheme.colorScheme.secondary,
              style = TextStyle(
                fontSize = (12.sp * fontScale),
                fontFamily = FontFamily.SansSerif
              )
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(30.dp).navigationBarsPadding())
    }
  }
}

// 48dp+ interactive sizes & card outline stroke mappings
@Composable
fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@Composable
fun SearchPanel(viewModel: AcademiaViewModel, fontScale: Float, isActive: Boolean) {
  val focusManager = LocalFocusManager.current
  var localInput by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 32.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "A C A D E M I A",
      color = MaterialTheme.colorScheme.onBackground,
      style = TextStyle(
        fontSize = (32.sp * fontScale),
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Light,
        letterSpacing = 0.25.sp
      ),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(40.dp))

    // Centered bottom bordered custom search bar
    Box(
      modifier = Modifier
        .widthIn(max = 480.dp)
        .fillMaxWidth()
        .drawBehind {
          val stroke = 1.dp.toPx()
          drawLine(
            color = if (localInput.isNotEmpty()) LightAccent else LightBorder,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = stroke
          )
        }
        .padding(vertical = 8.dp)
    ) {
      if (localInput.isEmpty()) {
        Text(
          text = "输入关键词，回车过滤检索并跳转...",
          color = MaterialTheme.colorScheme.secondary,
          style = TextStyle(
            fontSize = (15.sp * fontScale),
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      BasicTextField(
        value = localInput,
        onValueChange = { localInput = it },
        singleLine = true,
        textStyle = TextStyle(
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = (16.sp * fontScale),
          fontFamily = FontFamily.SansSerif,
          textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
          imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
          onSearch = {
            focusManager.clearFocus()
            val query = localInput.trim()
            if (query.isNotEmpty()) {
              viewModel.setSearchTerm(query)
              viewModel.showToast("已应用检索词: \"$query\"")
              viewModel.setCurrentPanel(PanelType.CATALOG)
            } else {
              viewModel.setSearchTerm("")
              viewModel.setCurrentPanel(PanelType.CATALOG)
            }
          }
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

// -------------------------------------------------------------
// Reader View Screen Overlay
// -------------------------------------------------------------

@Composable
fun ReaderScreen(viewModel: AcademiaViewModel, fontScale: Float) {
  val docId by viewModel.activeReaderDocId.collectAsState()
  val page by viewModel.readerPage.collectAsState()
  val language by viewModel.readerLanguage.collectAsState()
  val hudVisible by viewModel.readerHUDVisible.collectAsState()

  val content = remember(docId, language) { BilingualData.getPaperContent(docId, language) }
  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    val W = maxWidth
    val H = maxHeight

    // Horizontal manual drag for horizontally translating pages
    val readerOffsetX = remember { Animatable(0f) }
    var pageDragX by remember { mutableStateOf(0f) }
    var isPagingDrag by remember { mutableStateOf(false) }

    val threshold = remember(W) { calculateThreshold(W.value, H.value) }

    // Sync active page translation rest target offsets
    LaunchedEffect(page, W) {
      readerOffsetX.animateTo(
        targetValue = - (page - 1) * W.value,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
      )
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(page, W) {
          awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            var isDraggingDetected = false
            val startTime = System.currentTimeMillis()
            val startPosition = down.position

            while (true) {
              val event = awaitPointerEvent()
              val anyPressed = event.changes.any { it.pressed }
              if (!anyPressed) {
                // Up event - drag ended or user tapped
                val duration = System.currentTimeMillis() - startTime
                val endPos = event.changes.firstOrNull()?.position ?: startPosition
                val distX = endPos.x - startPosition.x
                val distY = endPos.y - startPosition.y
                val dist = abs(distX) + abs(distY)

                val isConsumed = event.changes.any { it.isConsumed }
                if (!isDraggingDetected && duration < 250 && dist < 15f && !isConsumed) {
                  viewModel.toggleReaderHUD()
                }

                if (isDraggingDetected) {
                  isPagingDrag = false
                  coroutineScope.launch {
                    if (pageDragX >= threshold && page > 1) {
                      viewModel.setReaderPage(page - 1)
                    } else if (pageDragX <= -threshold && page < 3) {
                      viewModel.setReaderPage(page + 1)
                    } else {
                      readerOffsetX.animateTo(- (page - 1) * W.value)
                    }
                  }
                }
                break
              }

              val change = event.changes.firstOrNull { it.id == down.id }
              if (change != null) {
                val currentPos = change.position
                val diffX = currentPos.x - startPosition.x
                val diffY = currentPos.y - startPosition.y

                if (!isDraggingDetected) {
                  if (abs(diffX) > 15f || abs(diffY) > 15f) {
                    if (abs(diffX) > abs(diffY)) {
                      isDraggingDetected = true
                      isPagingDrag = true
                      pageDragX = 0f
                    } else {
                      // Vertical scroll, don't drag so that nested scrollable handles it
                      break
                    }
                  }
                }

                if (isDraggingDetected) {
                  change.consume()
                  val dx = currentPos.x - change.previousPosition.x
                  val dxDp = with(density) { dx.toDp().value }
                  pageDragX += dxDp

                  val baseOffsetVal = - (page - 1) * W.value
                  val boundaryMultiplier = if ((page == 1 && pageDragX > 0) || (page == 3 && pageDragX < 0)) 0.4f else 1.0f
                  val dampedDrag = applyHighResistance(pageDragX, boundaryMultiplier)

                  coroutineScope.launch {
                    readerOffsetX.snapTo(baseOffsetVal + dampedDrag)
                  }
                }
              }
            }
          }
        }
    ) {
      // 3 Continuous horizontal side-by-side slides
      Box(
        modifier = Modifier
          .fillMaxSize()
          .offset { IntOffset(readerOffsetX.value.dp.roundToPx(), 0) }
      ) {
        // Page 1 Viewport
        Box(
          modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, 0) }
            .padding(horizontal = 24.dp)
        ) {
          ReaderArticleContent(page = 1, docId = docId, content = content, fontScale = fontScale)
        }

        // Page 2 Viewport
        Box(
          modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(W.roundToPx(), 0) }
            .padding(horizontal = 24.dp)
        ) {
          ReaderArticleContent(page = 2, docId = docId, content = content, fontScale = fontScale)
        }

        // Page 3 Viewport
        Box(
          modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset((W * 2).roundToPx(), 0) }
            .padding(horizontal = 24.dp)
        ) {
          ReaderArticleContent(page = 3, docId = docId, content = content, fontScale = fontScale)
        }
      }

      // Overlaid floating Reader HUD Corner Capsules
      HUDOverlay(
        viewModel = viewModel,
        page = page,
        hudVisible = hudVisible,
        docTitle = content.title,
        fontScale = fontScale
      )

      // Reader manual horizontal swipe boundaries indicators
      ReaderSwipeIndicators(
        page = page,
        isDragging = isPagingDrag,
        dragX = pageDragX,
        threshold = threshold
      )
    }
  }
}

@Composable
fun ReaderArticleContent(page: Int, docId: String, content: BilingualData.AcademicContent, fontScale: Float) {
  // Use unique key or Crossfade on contents to give smooth article crossfading transitions
  Crossfade(targetState = content, animationSpec = spring()) { articleContent ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(
          top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp,
          bottom = 96.dp
        ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Column(
        modifier = Modifier
          .widthIn(max = 640.dp)
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        if (page == 1) {
          Text(
            text = articleContent.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (24.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Medium,
              lineHeight = 32.sp,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          )

          Text(
            text = articleContent.authors,
            color = MaterialTheme.colorScheme.secondary,
            style = TextStyle(
              fontSize = (13.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontStyle = FontStyle.Italic,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
          )

          Text(
            text = articleContent.section1Title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (16.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 6.dp)
          )

          Text(
            text = "    " + articleContent.p1_1,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )

          Text(
            text = "    " + articleContent.p1_2,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )
        }

        if (page == 2) {
          Text(
            text = articleContent.section2Title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (16.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 6.dp)
          )

          Text(
            text = "    " + articleContent.p2_1,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )

          // Mathematical formula block
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
              .padding(vertical = 16.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = articleContent.formula,
              color = MaterialTheme.colorScheme.primary,
              style = TextStyle(
                fontSize = (17.sp * fontScale),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
              )
            )
          }

          Text(
            text = "    " + articleContent.p2_2,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )
        }

        if (page == 3) {
          Text(
            text = articleContent.section3Title,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (16.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 6.dp)
          )

          Text(
            text = "    " + articleContent.p3_1,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )

          Text(
            text = "    " + articleContent.p3_2,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (14.sp * fontScale),
              fontFamily = FontFamily.Serif,
              lineHeight = 26.sp,
              textAlign = TextAlign.Justify
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(MaterialTheme.colorScheme.outline)
          )

          Text(
            text = articleContent.refsTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
              fontSize = (15.sp * fontScale),
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
          )

          articleContent.referenceList.forEach { citation ->
            Text(
              text = citation,
              color = MaterialTheme.colorScheme.secondary,
              style = TextStyle(
                fontSize = (12.sp * fontScale),
                fontFamily = FontFamily.Serif,
                lineHeight = 18.sp
              ),
              modifier = Modifier.padding(bottom = 8.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun HUDOverlay(viewModel: AcademiaViewModel, page: Int, hudVisible: Boolean, docTitle: String, fontScale: Float) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    // 1. Top-Left Corner: Back to Catalog
    AnimatedVisibility(
      visible = hudVisible,
      enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
              slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)),
      exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
             slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessMedium)),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(top = topInset + 16.dp, start = 16.dp)
    ) {
      HUDBtn("返回目录", { FontAwesomeBackIcon() }, fontScale) {
        viewModel.closeReader()
      }
    }

    // 2. Top-Right Corner: Translate / Switch language
    AnimatedVisibility(
      visible = hudVisible,
      enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
              slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)),
      exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
             slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessMedium)),
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = topInset + 16.dp, end = 16.dp)
    ) {
      HUDBtn("EN / 中文", null, fontScale) {
        viewModel.toggleReaderLanguage()
      }
    }

    // 3. Bottom-Left Corner: Paper Info
    AnimatedVisibility(
      visible = hudVisible,
      enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
              slideInVertically(initialOffsetY = { it / 2 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)),
      exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
             slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessMedium)),
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(bottom = 24.dp, start = 16.dp)
    ) {
      HUDCapsule(docTitle, { FontAwesomeBookIcon() }, fontScale)
    }

    // 4. Bottom-Right Corner: Progression indicators & dots
    AnimatedVisibility(
      visible = hudVisible,
      enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
              slideInVertically(initialOffsetY = { it / 2 }, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)),
      exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) +
             slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = spring(stiffness = Spring.StiffnessMedium)),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 24.dp, end = 16.dp)
    ) {
      Row(
        modifier = Modifier
          .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
          .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(20.dp))
          .pointerInput(Unit) { detectTapGestures { } }
          .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Page Dot indicators
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 1..3) {
            val isActive = i == page
            val size = if (isActive) 7.dp else 4.dp
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .size(size)
                .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            )
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "$page / 3",
          color = MaterialTheme.colorScheme.onBackground,
          style = TextStyle(
            fontSize = (11.sp * fontScale),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium
          )
        )
      }
    }
  }
}

@Composable
fun HUDBtn(text: String, icon: (@Composable () -> Unit)? = null, fontScale: Float, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(MaterialTheme.colorScheme.surface)
      .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    if (icon != null) {
      icon()
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(
      text = text,
      color = MaterialTheme.colorScheme.onBackground,
      style = TextStyle(
        fontSize = (11.sp * fontScale),
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
fun HUDCapsule(text: String, icon: @Composable () -> Unit, fontScale: Float) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(MaterialTheme.colorScheme.surface)
      .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(20.dp))
      .pointerInput(Unit) { detectTapGestures { } }
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      color = MaterialTheme.colorScheme.secondary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = TextStyle(
        fontSize = (11.sp * fontScale),
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic
      ),
      modifier = Modifier.widthIn(max = 140.dp)
    )
  }
}

// -------------------------------------------------------------
// Interactive Navigation Edge Indicators
// -------------------------------------------------------------

@Composable
fun ActiveNavigationIndicators(
  currentPanel: PanelType,
  isDragging: Boolean,
  dragAxis: String?,
  dragX: Float,
  dragY: Float,
  threshold: Float
) {
  if (!isDragging || dragAxis == null) return

  val progressNum = if (dragAxis == "x") getDragProgress(dragX, threshold) else getDragProgress(dragY, threshold)
  val isPrimed = if (dragAxis == "x") abs(dragX) >= threshold else abs(dragY) >= threshold

  Box(modifier = Modifier.fillMaxSize()) {
    if (currentPanel == PanelType.CENTER) {
      if (dragAxis == "x") {
        if (dragX > 0) {
          // Left Edge indicator: reveal About system
          DragIndicator(
            label = "关于系统",
            progress = progressNum,
            isPrimed = isPrimed,
            alignment = Alignment.CenterStart
          )
        } else if (dragX < 0) {
          // Right Edge indicator: reveal Settings preferences
          DragIndicator(
            label = "系统设置",
            progress = progressNum,
            isPrimed = isPrimed,
            alignment = Alignment.CenterEnd
          )
        }
      } else if (dragAxis == "y") {
        if (dragY > 0) {
          // Top Edge indicator: reveal Catalog archive
          DragIndicator(
            label = "文献目录",
            progress = progressNum,
            isPrimed = isPrimed,
            alignment = Alignment.TopCenter
          )
        } else if (dragY < 0) {
          // Bottom Edge indicator: reveal content management
          DragIndicator(
            label = "内容管理",
            progress = progressNum,
            isPrimed = isPrimed,
            alignment = Alignment.BottomCenter
          )
        }
      }
    } else {
      // Returns back to center indications
      if (currentPanel == PanelType.ABOUT && dragX < 0) {
        DragIndicator(
          label = "返回中央仪表",
          progress = progressNum,
          isPrimed = isPrimed,
          alignment = Alignment.CenterEnd
        )
      } else if (currentPanel == PanelType.SETTINGS && dragX > 0) {
        DragIndicator(
          label = "返回中央仪表",
          progress = progressNum,
          isPrimed = isPrimed,
          alignment = Alignment.CenterStart
        )
      } else if (currentPanel == PanelType.CATALOG && dragY < 0) {
        DragIndicator(
          label = "返回中央仪表",
          progress = progressNum,
          isPrimed = isPrimed,
          alignment = Alignment.BottomCenter
        )
      } else if (currentPanel == PanelType.MANAGER && dragY > 0) {
        DragIndicator(
          label = "返回中央仪表",
          progress = progressNum,
          isPrimed = isPrimed,
          alignment = Alignment.TopCenter
        )
      }
    }
  }
}

@Composable
fun ReaderSwipeIndicators(
  page: Int,
  isDragging: Boolean,
  dragX: Float,
  threshold: Float
) {
  if (!isDragging) return

  val progressVal = getDragProgress(dragX, threshold)
  val isPrimed = abs(dragX) >= threshold

  Box(modifier = Modifier.fillMaxSize()) {
    if (dragX > 0) {
      // Dragging right -> previous
      val isBoundary = page == 1
      val textStr = if (isBoundary) "已达首页 / FIRST PAGE" else "上一页 / PAGE ${page - 1}"
      DragIndicator(
        label = textStr,
        progress = progressVal,
        isPrimed = isPrimed,
        alignment = Alignment.CenterStart,
        isBoundaryLimit = isBoundary
      )
    } else if (dragX < 0) {
      // Dragging left -> next
      val isBoundary = page == 3
      val textStr = if (isBoundary) "已达末页 / END OF PAPER" else "下一页 / PAGE ${page + 1}"
      DragIndicator(
        label = textStr,
        progress = progressVal,
        isPrimed = isPrimed,
        alignment = Alignment.CenterEnd,
        isBoundaryLimit = isBoundary
      )
    }
  }
}

@Composable
fun DragIndicator(
  label: String,
  progress: Float,
  isPrimed: Boolean,
  alignment: Alignment,
  isBoundaryLimit: Boolean = false
) {
  val accentColor = MaterialTheme.colorScheme.primary
  val borderColor = MaterialTheme.colorScheme.outline
  val errorColor = ErrorRed

  val isVerticalEdge = (alignment == Alignment.CenterStart || alignment == Alignment.CenterEnd)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .alpha(progress.coerceAtLeast(0.1f))
  ) {
    val paddingValues = if (isVerticalEdge) {
      if (alignment == Alignment.CenterStart) {
        androidx.compose.foundation.layout.PaddingValues(start = 4.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
      } else {
        androidx.compose.foundation.layout.PaddingValues(start = 24.dp, end = 4.dp, top = 24.dp, bottom = 24.dp)
      }
    } else {
      androidx.compose.foundation.layout.PaddingValues(24.dp)
    }

    Column(
      modifier = Modifier
        .align(alignment)
        .padding(paddingValues)
        .then(
          if (isVerticalEdge) {
            Modifier
              .offset(x = if (alignment == Alignment.CenterStart) (-35).dp else 35.dp)
              .graphicsLayer {
                rotationZ = if (alignment == Alignment.CenterStart) -90f else 90f
              }
              .width(110.dp)
          } else {
            Modifier.fillMaxWidth()
          }
        ),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (alignment == Alignment.TopCenter || alignment == Alignment.CenterStart) {
        // Line on bottom or right side relative to margins
        IndicatorLine(progress, isPrimed, isBoundaryLimit)
        Spacer(modifier = Modifier.height(6.dp))
      }

      Text(
        text = label,
        color = if (isBoundaryLimit) errorColor else if (isPrimed) accentColor else MaterialTheme.colorScheme.secondary,
        style = TextStyle(
          fontSize = 11.sp,
          fontFamily = FontFamily.Serif,
          fontWeight = if (isPrimed) FontWeight.SemiBold else FontWeight.Normal,
          letterSpacing = if (isPrimed) 0.15.sp else 0.05.sp
        ),
        textAlign = TextAlign.Center
      )

      if (alignment == Alignment.BottomCenter || alignment == Alignment.CenterEnd) {
        Spacer(modifier = Modifier.height(6.dp))
        IndicatorLine(progress, isPrimed, isBoundaryLimit)
      }
    }
  }
}

@Composable
fun IndicatorLine(progress: Float, isPrimed: Boolean, isBoundaryLimit: Boolean) {
  val baseWidth = (24 + 36 * progress).dp
  val targetWidth = if (isPrimed || isBoundaryLimit) 68.dp else baseWidth
  val targetHeight = if (isPrimed || isBoundaryLimit) 3.dp else 2.dp
  val targetColor = if (isBoundaryLimit) ErrorRed else if (isPrimed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

  Box(
    modifier = Modifier
      .width(targetWidth)
      .height(targetHeight)
      .background(targetColor)
  )
}

// -------------------------------------------------------------
// Geometric Drawing Icon Helpers
// -------------------------------------------------------------

@Composable
fun FontAwesomeBookIcon() {
  androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
    val strokeWidth = 1.5.dp.toPx()
    val color = LightTextSecondary
    drawArc(
      color = color,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = false,
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    drawLine(
      color = color,
      start = Offset(0f, size.height * 0.3f),
      end = Offset(0f, size.height),
      strokeWidth = strokeWidth
    )
    drawLine(
      color = color,
      start = Offset(size.width, size.height * 0.3f),
      end = Offset(size.width, size.height),
      strokeWidth = strokeWidth
    )
    drawLine(
      color = color,
      start = Offset(size.width / 2, size.height * 0.3f),
      end = Offset(size.width / 2, size.height),
      strokeWidth = strokeWidth
    )
  }
}

@Composable
fun FontAwesomeBackIcon() {
  androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
    val strokeWidth = 1.8.dp.toPx()
    val color = LightTextPrimary
    drawLine(
      color = color,
      start = Offset(size.width, size.height / 2),
      end = Offset(0f, size.height / 2),
      strokeWidth = strokeWidth
    )
    drawLine(
      color = color,
      start = Offset(size.width * 0.4f, 0f),
      end = Offset(0f, size.height / 2),
      strokeWidth = strokeWidth
    )
    drawLine(
      color = color,
      start = Offset(size.width * 0.4f, size.height),
      end = Offset(0f, size.height / 2),
      strokeWidth = strokeWidth
    )
  }
}

// -------------------------------------------------------------
// Multi-device Math Scaling and Calculation Helpers
// -------------------------------------------------------------

fun calculateThreshold(width: Float, height: Float): Float {
  val basic = width * 0.35f
  return if (basic < 200f) basic else 200f
}

fun getDragProgress(value: Float, threshold: Float): Float {
  val absVal = abs(value)
  val deadzone = 30f
  if (absVal <= deadzone) return 0f
  return ((absVal - deadzone) / (threshold - deadzone)).coerceIn(0f, 1f)
}

fun applyHighResistance(delta: Float, limitMultiplier: Float = 1.0f): Float {
  val absD = abs(delta)
  val resistanceFactor = 1.8f * limitMultiplier
  return kotlin.math.sign(delta) * absD.pow(0.45f) * resistanceFactor
}

// Custom Center Alignment mapper for standard Compose layouts
fun Alignment.Companion.CenterAlignmentLineMap(): Alignment.Horizontal? {
  return Alignment.CenterHorizontally
}
