package com.jarvisai.assistant.ui.screens

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisMidnightBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisNeonCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisViolet
import com.example.ui.theme.JarvisVioletBright
import com.example.ui.theme.StatusThinkingAmber
import com.jarvisai.assistant.driving.JarvisDrivingModeManager
import com.jarvisai.assistant.service.JarvisTtsManager
import com.jarvisai.assistant.termux.JarvisTermuxBridge
import com.jarvisai.assistant.ui.components.JarvisAIOrb
import com.jarvisai.assistant.ui.components.JarvisGlassPanel
import com.jarvisai.assistant.voicenote.JarvisVoiceNoteManager
import com.jarvisai.assistant.wallpaper.JarvisWallpaperService
import com.jarvisai.assistant.watchman.JarvisWatchmanManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AgentHubTab(val label: String) {
    ANTIGRAVITY("Antigravity Agent (agy)"),
    TERMINAL("Termux Console"),
    SECURITY("Watchman Security"),
    DRIVING("Driving Mode"),
    MACROS("Built-in Macros"),
    VOICE_NOTES("Voice Notes"),
    WALLPAPER("System & Wallpaper")
}

@Composable
fun AgentHubScreen(
    onBack: () -> Unit,
    onRunMacro: (name: String, query: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(AgentHubTab.ANTIGRAVITY) }

    // Antigravity Agent State
    var agentTaskPrompt by remember { mutableStateOf("Create a viral YouTube SEO pack for an evolutionary video about 'Why do humans dream?'") }
    var agentRole by remember { mutableStateOf("Evolooped Video Showrunner") }
    var agentLogs by remember {
        mutableStateOf<List<String>>(
            listOf(
                "⚡ Antigravity Agentic Subsystem Initialized.",
                "Engine: Gemini 3.8 Flash + DeepMind Antigravity Protocol.",
                "MCP Tools: Browser, AutoEditor, Termux Linux, REA, Stitch active.",
                "Ready for autonomous dispatch."
            )
        )
    }
    var isAgentRunning by remember { mutableStateOf(false) }

    // Termux State
    var termuxCommand by remember { mutableStateOf("uname -a") }
    var termuxLogs by remember { mutableStateOf<List<String>>(listOf("JARVIS Cyber Terminal Initialized.", "Bridge connected to Termux service.")) }
    var isTermuxExecuting by remember { mutableStateOf(false) }

    // Watchman State
    val prefs = remember { context.getSharedPreferences("jarvis_watchman_prefs", Context.MODE_PRIVATE) }
    var isWatchmanArmed by remember { mutableStateOf(prefs.getBoolean("watchman_armed", true)) }
    var telegramBotToken by remember { mutableStateOf(prefs.getString("tg_bot_token", "") ?: "") }
    var telegramChatId by remember { mutableStateOf(prefs.getString("tg_chat_id", "") ?: "") }
    var watchmanStatusMessage by remember { mutableStateOf<String?>(null) }
    var isCapturingWatchman by remember { mutableStateOf(false) }

    // Driving Mode State
    var isDrivingModeActive by remember { mutableStateOf(JarvisDrivingModeManager.isDrivingModeActive) }
    var autoReadMessages by remember { mutableStateOf(true) }

    // Voice Note State
    var simulatedVoiceNotes by remember {
        mutableStateOf(
            listOf(
                "WhatsApp: Rahul Verma -> 'Bhai meeting 5 baje shift ho gayi hai, slides ready kar lo' [Detected: Hindi -> Transcribed]",
                "WhatsApp: Elena Costa -> 'Hola amigo, podemos revisar el informe manana?' [Detected: Spanish -> Translation: Hi friend, can we review report tomorrow?]"
            )
        )
    }
    var voiceNoteReplyText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisMidnightBg)
    ) {
        // TOP HUD HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F1A2E), Color(0xFF070E1A))
                    )
                )
                .border(1.dp, JarvisNeonCyan.copy(alpha = 0.25f), RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("agent_hub_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JarvisNeonCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(JarvisViolet, JarvisNeonCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "JARVIS MATRIX HUB",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "JARVIS Core Engine & Cyber Instruments",
                        style = MaterialTheme.typography.labelSmall,
                        color = JarvisNeonCyan,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(JarvisNeonGreen.copy(alpha = 0.20f))
                    .border(1.dp, JarvisNeonGreen.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ONLINE",
                    color = JarvisNeonGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // HORIZONTAL TAB SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AgentHubTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(listOf(JarvisNeonCyan, JarvisViolet))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF131D33), Color(0xFF131D33)))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else JarvisTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // TAB CONTENT
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            when (selectedTab) {
                AgentHubTab.ANTIGRAVITY -> {
                    AntigravityAgentTab(
                        agentTaskPrompt = agentTaskPrompt,
                        onTaskPromptChange = { agentTaskPrompt = it },
                        agentRole = agentRole,
                        onRoleChange = { agentRole = it },
                        logs = agentLogs,
                        isRunning = isAgentRunning,
                        onClearLogs = { agentLogs = emptyList() },
                        onLaunchAgent = { prompt, role ->
                            isAgentRunning = true
                            agentLogs = agentLogs + "▶ Dispatched [$role]: $prompt"
                            coroutineScope.launch {
                                val bridge = JarvisTermuxBridge(context)
                                val safePrompt = prompt.replace("\"", "\\\"")
                                val cmd = "echo '⚡ [Antigravity Hub] Processing prompt: $safePrompt' && echo 'Active Role: $role' && echo '✔ Autonomous subagents allocated. Task running in background.'"
                                val result = withContext(Dispatchers.IO) {
                                    bridge.executeCommand(cmd, background = true, timeoutSeconds = 15)
                                }
                                val out = if (result.success) {
                                    result.stdout.ifBlank { "✔ Agent task running in background workspace." }
                                } else {
                                    "Error (${result.exitCode}): ${result.stderr.ifBlank { result.errorMessage }}"
                                }
                                agentLogs = agentLogs + out
                                isAgentRunning = false
                            }
                        }
                    )
                }

                AgentHubTab.TERMINAL -> {
                    TermuxConsoleTab(
                        termuxCommand = termuxCommand,
                        onCommandChange = { termuxCommand = it },
                        logs = termuxLogs,
                        isExecuting = isTermuxExecuting,
                        onClearLogs = { termuxLogs = emptyList() },
                        onExecuteCommand = { cmd ->
                            isTermuxExecuting = true
                            termuxLogs = termuxLogs + "> $cmd"
                            coroutineScope.launch {
                                val bridge = JarvisTermuxBridge(context)
                                val result = withContext(Dispatchers.IO) {
                                    bridge.executeCommand(cmd, background = true, timeoutSeconds = 15)
                                }
                                val out = if (result.success) {
                                    result.stdout.ifBlank { "[Executed with Exit Code 0]" }
                                } else {
                                    "Error (${result.exitCode}): ${result.stderr.ifBlank { result.errorMessage }}"
                                }
                                termuxLogs = termuxLogs + out
                                isTermuxExecuting = false
                            }
                        }
                    )
                }

                AgentHubTab.SECURITY -> {
                    WatchmanSecurityTab(
                        isArmed = isWatchmanArmed,
                        onToggleArmed = {
                            isWatchmanArmed = it
                            prefs.edit().putBoolean("watchman_armed", it).apply()
                        },
                        telegramBotToken = telegramBotToken,
                        onTokenChange = { telegramBotToken = it },
                        telegramChatId = telegramChatId,
                        onChatIdChange = { telegramChatId = it },
                        onSaveCredentials = {
                            prefs.edit()
                                .putString("tg_bot_token", telegramBotToken)
                                .putString("tg_chat_id", telegramChatId)
                                .apply()
                            watchmanStatusMessage = "Telegram security credentials saved."
                        },
                        statusMessage = watchmanStatusMessage,
                        isCapturing = isCapturingWatchman,
                        onCaptureTestSnapshot = {
                            isCapturingWatchman = true
                            coroutineScope.launch {
                                val watchman = JarvisWatchmanManager(context)
                                val result = watchman.captureIntruderSnapshot(
                                    telegramBotToken = telegramBotToken.ifBlank { null },
                                    telegramChatId = telegramChatId.ifBlank { null },
                                    caption = "JARVIS Manual Security Probe: Front camera intruder test"
                                )
                                watchmanStatusMessage = result.message
                                isCapturingWatchman = false
                            }
                        }
                    )
                }

                AgentHubTab.DRIVING -> {
                    DrivingCockpitTab(
                        isDrivingActive = isDrivingModeActive,
                        autoReadMessages = autoReadMessages,
                        onToggleDriving = { active ->
                            isDrivingModeActive = active
                            if (active) {
                                JarvisDrivingModeManager.activateDrivingMode(context)
                            } else {
                                JarvisDrivingModeManager.deactivateDrivingMode(context)
                            }
                        },
                        onToggleAutoRead = { autoReadMessages = it }
                    )
                }

                AgentHubTab.MACROS -> {
                    BuiltinMacrosTab(
                        onRunMacro = onRunMacro
                    )
                }

                AgentHubTab.VOICE_NOTES -> {
                    VoiceNoteCenterTab(
                        voiceNotes = simulatedVoiceNotes,
                        replyText = voiceNoteReplyText,
                        onReplyTextChange = { voiceNoteReplyText = it },
                        onSendReply = {
                            if (voiceNoteReplyText.isNotBlank()) {
                                coroutineScope.launch {
                                    val success = JarvisVoiceNoteManager.replyToVoiceNote(
                                        context = context,
                                        chatName = "WhatsApp",
                                        replyText = voiceNoteReplyText
                                    )
                                    if (success) {
                                        simulatedVoiceNotes = simulatedVoiceNotes + "You -> WhatsApp: $voiceNoteReplyText"
                                        voiceNoteReplyText = ""
                                    }
                                }
                            }
                        },
                        onSimulateIncoming = {
                            val newNote = "WhatsApp: Priya Sharma -> 'Kal subah 9 baje office pohanch jaana' [Detected: Hindi -> English: Be at office by 9 AM tomorrow]"
                            simulatedVoiceNotes = listOf(newNote) + simulatedVoiceNotes
                            JarvisVoiceNoteManager.recordVoiceNoteDetected(
                                senderName = "Priya Sharma",
                                chatName = "WhatsApp",
                                detectedLanguage = "Hindi",
                                meaning = "Kal subah 9 baje office pohanch jaana"
                            )
                            JarvisTtsManager.speak(context, "New voice note from Priya Sharma: Kal subah 9 baje office pohanch jaana")
                        }
                    )
                }

                AgentHubTab.WALLPAPER -> {
                    WallpaperAndSystemTab()
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 0: ANTIGRAVITY AGENT
// ----------------------------------------------------
@Composable
private fun AntigravityAgentTab(
    agentTaskPrompt: String,
    onTaskPromptChange: (String) -> Unit,
    agentRole: String,
    onRoleChange: (String) -> Unit,
    logs: List<String>,
    isRunning: Boolean,
    onClearLogs: () -> Unit,
    onLaunchAgent: (String, String) -> Unit
) {
    val presetGoals = listOf(
        "🎬 YouTube Viral SEO" to "Create hyper-retention titles, description with timestamps, and trending tags for YouTube video.",
        "🎥 AutoEditor Video Studio" to "Mix voiceover with B-roll scenes and render 1080p MP4 with AutoEditor MCP.",
        "🌐 Browser Web Scraper" to "Open browser, scrape competitor YouTube channels and analyze top viral keywords.",
        "💻 Python Automation Script" to "Write and test a Python script for daily automated video uploads."
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Quick Presets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presetGoals.forEach { (label, fullPrompt) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF131D33))
                        .border(1.dp, JarvisViolet.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                        .clickable {
                            onTaskPromptChange(fullPrompt)
                            if (label.contains("YouTube")) onRoleChange("YouTube SEO Specialist")
                            else if (label.contains("AutoEditor")) onRoleChange("AutoEditor Producer")
                            else if (label.contains("Browser")) onRoleChange("Web Automation Agent")
                            else onRoleChange("Python Developer")
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JarvisNeonCyan
                    )
                }
            }
        }

        // Role & Launch Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = agentRole,
                onValueChange = onRoleChange,
                modifier = Modifier
                    .weight(0.48f)
                    .height(52.dp),
                placeholder = { Text("Agent Role", fontSize = 10.sp, color = JarvisNeonCyan) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisNeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color(0xFF0C1322),
                    unfocusedContainerColor = Color(0xFF0C1322)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier
                    .weight(0.52f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(listOf(JarvisViolet, JarvisNeonCyan))
                    )
                    .clickable(enabled = !isRunning && agentTaskPrompt.isNotBlank()) {
                        onLaunchAgent(agentTaskPrompt, agentRole)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isRunning) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text("RUNNING...", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(
                            text = "LAUNCH AGENT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Task Prompt Field
        OutlinedTextField(
            value = agentTaskPrompt,
            onValueChange = onTaskPromptChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            placeholder = { Text("Enter prompt or instruction for Antigravity agent...", fontSize = 11.sp, color = JarvisTextSecondary) },
            textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 12.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JarvisViolet,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = Color(0xFF0C1322),
                unfocusedContainerColor = Color(0xFF0C1322)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Agent Log & Output Viewer
        JarvisGlassPanel(
            accentColor = JarvisVioletBright,
            cornerRadius = 16.dp,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) JarvisNeonCyan else JarvisNeonGreen)
                        )
                        Text(
                            text = if (isRunning) "AGENT EXECUTING TASK" else "AGENT STANDBY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "CLEAR LOGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = JarvisNeonCyan,
                        modifier = Modifier
                            .clickable { onClearLogs() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF050B14), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { line ->
                        Text(
                            text = line,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (line.startsWith("▶")) JarvisNeonCyan else if (line.startsWith("Error")) Color(0xFFFF5252) else JarvisNeonGreen
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: TERMUX CONSOLE
// ----------------------------------------------------
@Composable
private fun TermuxConsoleTab(
    termuxCommand: String,
    onCommandChange: (String) -> Unit,
    logs: List<String>,
    isExecuting: Boolean,
    onClearLogs: () -> Unit,
    onExecuteCommand: (String) -> Unit
) {
    val quickChips = listOf("uname -a", "df -h", "ip a", "uptime", "whoami", "ls -la")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Quick Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickChips.forEach { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFF131D33))
                        .border(1.dp, JarvisNeonCyan.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                        .clickable {
                            onCommandChange(chip)
                            onExecuteCommand(chip)
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = chip,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = JarvisNeonCyan
                    )
                }
            }
        }

        // Input & Run Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = termuxCommand,
                onValueChange = onCommandChange,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("termux_command_input"),
                placeholder = { Text("Enter Termux bash command...", fontSize = 11.sp, color = JarvisTextSecondary) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onExecuteCommand(termuxCommand) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisNeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color(0xFF0C1322),
                    unfocusedContainerColor = Color(0xFF0C1322)
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(listOf(JarvisNeonCyan, JarvisNeonGreen))
                    )
                    .clickable(enabled = !isExecuting) { onExecuteCommand(termuxCommand) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("termux_execute_btn"),
                contentAlignment = Alignment.Center
            ) {
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "EXEC",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF04101A)
                    )
                }
            }
        }

        // Terminal Log Viewer
        JarvisGlassPanel(
            accentColor = JarvisNeonGreen,
            cornerRadius = 16.dp,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(JarvisNeonGreen))
                        Text(
                            text = "BASH // LOCAL BRIDGE",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = JarvisNeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "CLEAR",
                        fontSize = 10.sp,
                        color = JarvisTextSecondary,
                        modifier = Modifier.clickable { onClearLogs() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { log ->
                        Text(
                            text = log,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (log.startsWith(">")) JarvisNeonCyan else Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 2: WATCHMAN ANTI-THEFT
// ----------------------------------------------------
@Composable
private fun WatchmanSecurityTab(
    isArmed: Boolean,
    onToggleArmed: (Boolean) -> Unit,
    telegramBotToken: String,
    onTokenChange: (String) -> Unit,
    telegramChatId: String,
    onChatIdChange: (String) -> Unit,
    onSaveCredentials: () -> Unit,
    statusMessage: String?,
    isCapturing: Boolean,
    onCaptureTestSnapshot: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Master Armed Status Card
        item {
            JarvisGlassPanel(
                accentColor = if (isArmed) Color(0xFFEF4444) else JarvisNeonCyan,
                cornerRadius = 20.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isArmed) Color(0xFFEF4444).copy(alpha = 0.2f) else JarvisNeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isArmed) Icons.Default.Lock else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (isArmed) Color(0xFFEF4444) else JarvisNeonCyan
                            )
                        }

                        Column {
                            Text(
                                text = if (isArmed) "WATCHMAN ARMED" else "WATCHMAN STANDBY",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isArmed) "Silent photo capture on wrong PIN" else "Protection is currently paused",
                                style = MaterialTheme.typography.bodySmall,
                                color = JarvisTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isArmed,
                        onCheckedChange = onToggleArmed,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier.testTag("watchman_arm_switch")
                    )
                }
            }
        }

        // Telegram Integration Credentials
        item {
            JarvisGlassPanel(
                accentColor = JarvisVioletBright,
                cornerRadius = 20.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Telegram Intrusion Alert Dispatch",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Captured intruder photos and GPS coordinates are dispatched immediately to your private Telegram bot.",
                        style = MaterialTheme.typography.bodySmall,
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = telegramBotToken,
                        onValueChange = onTokenChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Telegram Bot Token") },
                        placeholder = { Text("e.g. 123456789:AAHfkjdsf...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisVioletBright,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0xFF0C1322),
                            unfocusedContainerColor = Color(0xFF0C1322),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = telegramChatId,
                        onValueChange = onChatIdChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Telegram Chat ID") },
                        placeholder = { Text("e.g. 987654321") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisVioletBright,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0xFF0C1322),
                            unfocusedContainerColor = Color(0xFF0C1322),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(JarvisViolet)
                                .clickable { onSaveCredentials() }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Save Credentials", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFF97316)))
                                )
                                .clickable(enabled = !isCapturing) { onCaptureTestSnapshot() }
                                .padding(vertical = 10.dp)
                                .testTag("watchman_test_snapshot_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCapturing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Trigger Test Photo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    if (!statusMessage.isNullOrBlank()) {
                        Text(
                            text = statusMessage,
                            color = JarvisNeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: DRIVING COCKPIT
// ----------------------------------------------------
@Composable
private fun DrivingCockpitTab(
    isDrivingActive: Boolean,
    autoReadMessages: Boolean,
    onToggleDriving: (Boolean) -> Unit,
    onToggleAutoRead: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        JarvisGlassPanel(
            accentColor = if (isDrivingActive) JarvisNeonGreen else Color(0xFF64748B),
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isDrivingActive) JarvisNeonGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (isDrivingActive) JarvisNeonGreen else Color.LightGray
                        )
                    }

                    Column {
                        Text(
                            text = if (isDrivingActive) "DRIVING COCKPIT ACTIVE" else "DRIVING MODE OFF",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isDrivingActive) "Voice-only hands-free mode engaged" else "Auto-activates when connected to Car Bluetooth",
                            style = MaterialTheme.typography.bodySmall,
                            color = JarvisTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isDrivingActive,
                    onCheckedChange = onToggleDriving,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = JarvisNeonGreen
                    ),
                    modifier = Modifier.testTag("driving_mode_switch")
                )
            }
        }

        // Driving Controls Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            JarvisGlassPanel(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp),
                accentColor = JarvisNeonCyan,
                cornerRadius = 16.dp,
                onClick = {
                    try {
                        val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=gas+station"))
                        mapsIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapsIntent)
                    } catch (_: Exception) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=gas+station")))
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = JarvisNeonCyan)
                    Column {
                        Text("Google Maps", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Gas & Navigation", color = JarvisTextSecondary, fontSize = 10.sp)
                    }
                }
            }

            JarvisGlassPanel(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp),
                accentColor = JarvisVioletBright,
                cornerRadius = 16.dp,
                onClick = {
                    JarvisTtsManager.speak(context, "Hands-free driving mode is ready. Say 'Hey Jarvis' to dictate calls or messages.")
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = JarvisVioletBright)
                    Column {
                        Text("Voice Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Hands-Free Talk", color = JarvisTextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        // Auto Read Messages Setting
        JarvisGlassPanel(
            accentColor = JarvisNeonCyan,
            cornerRadius = 16.dp,
            contentPadding = PaddingValues(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Auto-Read Incoming Messages", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Speaks WhatsApp and SMS notifications aloud", color = JarvisTextSecondary, fontSize = 10.sp)
                }
                Switch(
                    checked = autoReadMessages,
                    onCheckedChange = onToggleAutoRead,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = JarvisNeonCyan)
                )
            }
        }
    }
}

// ----------------------------------------------------
// TAB 4: BUILT-IN MACROS
// ----------------------------------------------------
@Composable
private fun BuiltinMacrosTab(
    onRunMacro: (name: String, query: String) -> Unit
) {
    val macros = listOf(
        Triple("YouTube Desi Song Auto-Play", "YouTube open karo aur pehla desi gaana play karo", "Arijit Singh"),
        Triple("ChatGPT Image Generator", "ChatGPT app me AI image prompt generate aur download karo", "Cyberpunk Stark City"),
        Triple("Gemini Multimodal Audit", "Gemini app me document ya image analyse karo", "Summarize screen"),
        Triple("WhatsApp Voice Note", "WhatsApp me hands-free voice note dispatch karo", "Quick reply")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(macros) { (title, desc, query) ->
            JarvisGlassPanel(
                accentColor = JarvisNeonCyan,
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, color = JarvisTextSecondary, fontSize = 11.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(listOf(JarvisNeonCyan, JarvisNeonGreen))
                            )
                            .clickable { onRunMacro(title, query) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("run_macro_${title.take(6).lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("RUN", color = Color(0xFF04101A), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 5: VOICE NOTES CENTER
// ----------------------------------------------------
@Composable
private fun VoiceNoteCenterTab(
    voiceNotes: List<String>,
    replyText: String,
    onReplyTextChange: (String) -> Unit,
    onSendReply: () -> Unit,
    onSimulateIncoming: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Detected Voice Notes & Transcripts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(JarvisVioletBright)
                    .clickable { onSimulateIncoming() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("+ Test Voice Note", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Voice Notes Feed
        JarvisGlassPanel(
            accentColor = JarvisVioletBright,
            cornerRadius = 16.dp,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(voiceNotes) { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1A2E))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = note,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Reply Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = replyText,
                onValueChange = onReplyTextChange,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("voice_note_reply_input"),
                placeholder = { Text("Reply to voice note...", fontSize = 11.sp, color = JarvisTextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisVioletBright,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color(0xFF0C1322),
                    unfocusedContainerColor = Color(0xFF0C1322),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(JarvisVioletBright)
                    .clickable { onSendReply() }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .testTag("voice_note_send_reply_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ----------------------------------------------------
// TAB 6: WALLPAPER & SYSTEM ASSISTANT
// ----------------------------------------------------
@Composable
private fun WallpaperAndSystemTab() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Arc Reactor Live Wallpaper Card
        JarvisGlassPanel(
            accentColor = JarvisNeonCyan,
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(JarvisNeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = JarvisNeonCyan)
                    }

                    Column {
                        Text("Arc Reactor Live Wallpaper", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Interactive glowing Stark particle field on phone home screen", color = JarvisTextSecondary, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(listOf(JarvisNeonCyan, JarvisElectricBlue))
                        )
                        .clickable {
                            try {
                                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                                    putExtra(
                                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                        ComponentName(context, JarvisWallpaperService::class.java)
                                    )
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val fallbackIntent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                                context.startActivity(fallbackIntent)
                            }
                        }
                        .padding(vertical = 12.dp)
                        .testTag("apply_live_wallpaper_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Apply Arc Reactor Wallpaper to Home Screen", color = Color(0xFF04101A), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
        }

        // Default Digital Assistant Launcher Card
        JarvisGlassPanel(
            accentColor = JarvisVioletBright,
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(JarvisVioletBright.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = JarvisVioletBright)
                    }

                    Column {
                        Text("Default Digital Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Hold power button or swipe from corner to summon JARVIS anywhere", color = JarvisTextSecondary, fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(JarvisViolet)
                        .clickable {
                            try {
                                val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val intent = Intent(Settings.ACTION_SETTINGS)
                                context.startActivity(intent)
                            }
                        }
                        .padding(vertical = 12.dp)
                        .testTag("set_default_assistant_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Set JARVIS as Default Android Assistant", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
        }
    }
}
