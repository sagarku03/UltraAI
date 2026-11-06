package com.jarvisai.assistant.service

import org.json.JSONArray
import org.json.JSONObject

enum class ToolRiskLevel {
    READ_ONLY,                       // Zero risk (reading screen, getting battery info)
    LOW_RISK,                        // Low risk (opening app, scrolling, navigating home/back)
    SENSITIVE_CONFIRMATION_REQUIRED  // Consequential actions (sending messages, payments, deleting data)
}

data class ToolDefinition(
    val name: String,
    val description: String,
    val riskLevel: ToolRiskLevel,
    val requiredPermissions: List<String> = emptyList(),
    val parameterSchema: JSONObject
)

/**
 * Central tool registry providing validated JSON schemas, risk classifications,
 * and permission requirements for all JARVIS AI tools callable by Gemini Live.
 */
object ToolRegistry {

    const val TOOL_LAUNCH_APPLICATION = "launch_application"
    const val TOOL_GET_SCREEN_CONTEXT = "get_screen_context"
    const val TOOL_CLICK_ACCESSIBLE_ELEMENT = "click_accessible_element"
    const val TOOL_SCROLL_SCREEN = "scroll_screen"
    const val TOOL_NAVIGATE_BACK = "navigate_back"
    const val TOOL_NAVIGATE_HOME = "navigate_home"
    const val TOOL_SENSITIVE_ACTION_CONFIRMATION = "request_sensitive_action_confirmation"
    const val TOOL_DIRECTIONAL_GESTURE = "directional_gesture"
    const val TOOL_TYPE_TEXT = "type_text"
    const val TOOL_FOCUS_INPUT = "focus_input_field"
    const val TOOL_CLEAR_TEXT = "clear_text"
    const val TOOL_GLOBAL_ACTION = "global_action"
    const val TOOL_READ_SCREEN_TEXT = "read_screen_text"
    const val TOOL_GET_SYSTEM_TELEMETRY = "get_system_telemetry"
    const val TOOL_ADJUST_SYSTEM_VOLUME = "adjust_system_volume"
    const val TOOL_CONTROL_MEDIA_PLAYBACK = "control_media_playback"
    const val TOOL_CONTROL_DEVICE_SETTING = "control_device_setting"
    const val TOOL_SEND_COMMUNICATION = "send_communication_message"
    const val TOOL_OPEN_PHONE_DIALER = "open_phone_dialer"
    const val TOOL_OPEN_SPOTIFY_MUSIC = "open_spotify_music"

    // Top 10 Agentic Mobile Controller Tools (Live Voice Native)
    const val TOOL_RUN_AUTONOMOUS_GOAL = "run_autonomous_react_goal"
    const val TOOL_RUN_AUTONOMOUS_REACT_GOAL = TOOL_RUN_AUTONOMOUS_GOAL
    const val TOOL_TAP_SET_OF_MARK_BADGE = "tap_set_of_mark_badge"
    const val TOOL_CONTROL_FLOATING_HUD = "control_floating_hud"
    const val TOOL_REPLY_TO_NOTIFICATION = "reply_to_notification"
    const val TOOL_FILL_SCREEN_FORM = "fill_screen_form"
    const val TOOL_CROSS_APP_SCREEN_BRIDGE = "cross_app_screen_bridge"
    const val TOOL_MANAGE_LEARNING_MODE = "manage_learning_mode"
    const val TOOL_SET_ALARM_OR_TIMER = "set_alarm_or_timer"
    const val TOOL_DEEP_LINK_TELEPORT = "deep_link_teleport"
    const val TOOL_VOICE_UNDO_OR_STOP = "voice_undo_or_stop"

    // JARVIS Supercharged Systems (System Integration & Telephony)
    const val TOOL_EXECUTE_TERMUX_COMMAND = "execute_termux_command"
    const val TOOL_ANSWER_OR_END_CALL = "answer_or_end_call"
    const val TOOL_TRIGGER_WATCHMAN_ALERT = "trigger_watchman_alert"
    const val TOOL_REPLY_TO_VOICE_NOTE = "reply_to_voice_note"
    const val TOOL_EXECUTE_BUILTIN_MACRO = "execute_builtin_macro"
    const val TOOL_OPEN_IN_APP_BROWSER = "open_in_app_browser"
    const val TOOL_INSPECT_VISUAL_SCREEN = "inspect_visual_screen"

    // Antigravity Autonomous Agent & Linux Engine Tools
    const val TOOL_DISPATCH_ANTIGRAVITY_AGENT = "dispatch_antigravity_agent"
    const val TOOL_GET_ANTIGRAVITY_STATUS = "get_antigravity_status"
    const val TOOL_RUN_NATIVE_LINUX_COMMAND = "run_native_linux_command"

    // Next-Gen Autonomous Agentic Tools (Audit & Top 10 Features Upgrade)
    const val TOOL_AUTO_FILL_OTP = "auto_fill_otp"
    const val TOOL_COMPARE_PRICES = "compare_prices_across_apps"
    const val TOOL_SCROLL_STITCH_READER = "scroll_stitch_page_reader"
    const val TOOL_REGISTER_SENTINEL_RULE = "register_sentinel_rule"
    const val TOOL_WEB_DOM_ACTION = "execute_web_dom_action"
    const val TOOL_SCREEN_CALL_FOLLOWUP = "screen_call_with_followup"

    val ALL_TOOLS: List<ToolDefinition> = listOf(
        ToolDefinition(
            name = TOOL_LAUNCH_APPLICATION,
            description = "Launches an installed Android app (YouTube, Chrome, Settings, Calculator, WhatsApp, Spotify, etc.) on the user's phone. When the user asks to open YouTube, search a song/video, AND play the first (or Nth) video, set search_query and auto_play_first_result=true.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("app_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Display name of the Android app, e.g. 'YouTube', 'Chrome', 'Settings', 'Calculator'.")
                    })
                    put("package_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional Android package name if known.")
                    })
                    put("search_query", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional search query when opening YouTube or Chrome.")
                    })
                    put("auto_play_first_result", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "Set to true when the user asks to search on YouTube AND play/open the first video result automatically.")
                    })
                    put("ordinal_to_play", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Optional 1-based video result number to automatically play after searching (default 1 when auto_play_first_result=true).")
                    })
                })
                put("required", JSONArray().put("app_name"))
            }
        ),
        ToolDefinition(
            name = TOOL_GET_SCREEN_CONTEXT,
            description = "Inspects the current Android screen via JARVIS Accessibility Service and returns visible UI elements and active app package.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_CLICK_ACCESSIBLE_ELEMENT,
            description = "Clicks a visible button or UI element on the current Android screen using its text, contentDescription, or viewId.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("target_text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Visible text of the button or UI element to click.")
                    })
                    put("content_description", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional contentDescription of the button/icon.")
                    })
                    put("view_id", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional view resource ID.")
                    })
                    put("ordinal_index", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Optional 1-based position index (e.g., 1 for first item/video, 2 for second item/video) when user asks to open the Nth item.")
                    })
                    put("user_confirmed", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True only if the user explicitly confirmed a sensitive action.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_SCROLL_SCREEN,
            description = "Scrolls the current scrollable page up or down using the Accessibility Service.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("direction", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Direction to scroll: 'down' or 'up'.")
                    })
                })
                put("required", JSONArray().put("direction"))
            }
        ),
        ToolDefinition(
            name = TOOL_NAVIGATE_BACK,
            description = "Performs the Android global Back action.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_NAVIGATE_HOME,
            description = "Performs the Android global Home action returning to Home screen.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_SENSITIVE_ACTION_CONFIRMATION,
            description = "Records user confirmation for sensitive actions.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action_type", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Action category: 'SEND_MESSAGE', 'PAYMENT', 'DELETE_DATA', 'SECURITY_SETTING'.")
                    })
                    put("target_description", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Description of sensitive action to confirm with the user.")
                    })
                    put("user_confirmed", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True only after user explicitly confirmed.")
                    })
                })
                put("required", JSONArray().put("action_type").put("target_description"))
            }
        ),
        ToolDefinition(
            name = TOOL_DIRECTIONAL_GESTURE,
            description = "Performs directional screen gestures: 'scroll' or 'swipe' in direction 'up', 'down', 'left', 'right', or 'tap' / 'long_press' at coordinates (x, y).",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Action: 'scroll', 'swipe', 'tap', or 'long_press'.")
                    })
                    put("direction", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Direction: 'up', 'down', 'left', or 'right'.")
                    })
                    put("x", JSONObject().apply {
                        put("type", "NUMBER")
                        put("description", "Optional X screen coordinate in pixels for 'tap' or 'long_press'.")
                    })
                    put("y", JSONObject().apply {
                        put("type", "NUMBER")
                        put("description", "Optional Y screen coordinate in pixels for 'tap' or 'long_press'.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_TYPE_TEXT,
            description = "Types text into the active or targeted input field using Accessibility ACTION_SET_TEXT & clipboard fallback.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Text to input.")
                    })
                    put("target_label", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional label or hint of target field to focus before typing.")
                    })
                    put("append", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to append, false to replace existing text.")
                    })
                    put("replace_existing", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to replace existing text, false to append.")
                    })
                    put("submit", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to submit after entering text.")
                    })
                    put("submit_after", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to submit after entering text.")
                    })
                })
                put("required", JSONArray().put("text"))
            }
        ),
        ToolDefinition(
            name = TOOL_FOCUS_INPUT,
            description = "Focuses an editable input field by hint, label, or viewId.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("target_label", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Label or hint of field to focus.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_CLEAR_TEXT,
            description = "Clears text in the active or specified input field.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_GLOBAL_ACTION,
            description = "Performs an Android global action: 'back', 'home', 'recents', 'notifications', or 'quick_settings'.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Global action name: 'back', 'home', 'recents', 'notifications', 'quick_settings'.")
                    })
                })
                put("required", JSONArray().put("action"))
            }
        ),
        ToolDefinition(
            name = TOOL_READ_SCREEN_TEXT,
            description = "Reads and returns all visible text hierarchy from the current Android screen.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_GET_SYSTEM_TELEMETRY,
            description = "Reads current real-time device battery percentage, charging status, network connectivity (Wi-Fi/cellular), RAM, storage, and current date/time.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject())
            }
        ),
        ToolDefinition(
            name = TOOL_ADJUST_SYSTEM_VOLUME,
            description = "Adjusts device media audio volume: 'up', 'down', 'mute', or 'unmute'.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("direction", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Volume adjustment: 'up', 'down', 'mute', or 'unmute'.")
                    })
                })
                put("required", JSONArray().put("direction"))
            }
        ),
        ToolDefinition(
            name = TOOL_CONTROL_MEDIA_PLAYBACK,
            description = "Controls media playback on the device: 'play', 'pause', 'toggle', 'next', or 'previous'.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Media action: 'play', 'pause', 'toggle', 'next', 'previous'.")
                    })
                })
                put("required", JSONArray().put("action"))
            }
        ),
        ToolDefinition(
            name = TOOL_CONTROL_DEVICE_SETTING,
            description = "Directly turns ON or OFF Android hardware/system controls including Bluetooth, Wi-Fi, Mobile Data, Hotspot, Flashlight/Torch, or adjusts screen Brightness automatically via SDK & Accessibility Service.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("setting", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Device setting to control: 'bluetooth', 'wifi', 'mobile_data', 'hotspot', 'flashlight', or 'brightness'.")
                    })
                    put("enable", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to turn ON / enable, false to turn OFF / disable.")
                    })
                    put("level_percent", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Optional percentage (0-100) when setting is 'brightness'.")
                    })
                })
                put("required", JSONArray().put("setting"))
            }
        ),
        ToolDefinition(
            name = TOOL_SEND_COMMUNICATION,
            description = "Directly opens the target contact's chat in WhatsApp, Telegram, SMS, or Email, automatically types the message, and clicks Send without requiring confirmation.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("platform", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Platform: 'whatsapp', 'telegram', 'sms', or 'email'.")
                    })
                    put("recipient", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Recipient phone number, contact name (e.g. 'Mummy', 'Momi', 'Subhamkar'), or email address.")
                    })
                    put("message", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Message text body to type and send.")
                    })
                    put("subject", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional email subject line (for email).")
                    })
                    put("user_confirmed", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "Optional flag (always treated as true).")
                    })
                })
                put("required", JSONArray().put("platform").put("message"))
            }
        ),
        ToolDefinition(
            name = TOOL_OPEN_PHONE_DIALER,
            description = "Opens the Android phone dialer with the specified phone number or contact name loaded, optionally enabling speakerphone mode.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("phone_number", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Phone number or contact name to dial.")
                    })
                    put("contact_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional contact name to resolve from phonebook (e.g. 'Mummy', 'Rahul').")
                    })
                    put("speakerphone", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True if the user asked to place the call on speaker / speakerphone.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_OPEN_SPOTIFY_MUSIC,
            description = "Opens Spotify and searches for a song, artist, album, or playlist.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("search_query", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Song, artist, or playlist name to play on Spotify.")
                    })
                })
                put("required", JSONArray().put("search_query"))
            }
        ),
        // =========================================================================
        // TOP 10 AGENTIC MOBILE CONTROLLER TOOLS (LIVE VOICE NATIVE)
        // =========================================================================
        ToolDefinition(
            name = TOOL_RUN_AUTONOMOUS_GOAL,
            description = "Feature 1: Executes a multi-step autonomous Observe-Think-Act-Verify ReAct goal across apps (e.g., 'Open YouTube, search Arijit song and play the first video', 'Open Amazon and check my orders', 'Open Settings and check battery').",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("goal", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Full natural-language multi-step goal spoken by the user.")
                    })
                    put("app_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional primary target app name (e.g. 'YouTube', 'Amazon', 'WhatsApp').")
                    })
                    put("search_query", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional search query if the goal includes searching.")
                    })
                    put("play_ordinal", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Optional 1-based result index to click/play after searching (e.g. 1 for first video).")
                    })
                })
                put("required", JSONArray().put("goal"))
            }
        ),
        ToolDefinition(
            name = TOOL_TAP_SET_OF_MARK_BADGE,
            description = "Feature 2: Vision-Guided Set-of-Mark (SoM) screen tapping. Taps any labeled or unlabeled icon on the screen by badge number (e.g. badge_number=1, badge_number=2 shown in [1], [2] tags on screen), icon role ('search icon', 'profile icon', 'menu icon', 'close button', 'send button'), or screen region ('top right', 'bottom right').",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("target_spec", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Icon description, badge number, or screen position, e.g. 'search icon', 'badge 3', 'top right icon', 'send'.")
                    })
                    put("badge_number", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Optional 1-based Set-of-Mark badge number [1], [2], [3] visible on screen to tap directly.")
                    })
                    put("badge_id", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Alias for badge_number (1-based Set-of-Mark badge ID).")
                    })
                    put("target_hint", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Alias for target_spec (text or icon hint to tap).")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_CONTROL_FLOATING_HUD,
            description = "Feature 3: Turns ON or OFF the JARVIS Floating Cyber-HUD overlay bubble/pill on the phone screen. Always use this tool when the user says 'floating HUD on karo', 'folding HUD on karo', 'folding HD on karo', 'floating HD on karo', 'cyber HUD on karo', or 'floating bubble off karo'.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("enable", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True to turn ON the Floating Cyber-HUD overlay, false to turn it OFF.")
                    })
                })
                put("required", JSONArray().put("enable"))
            }
        ),
        ToolDefinition(
            name = TOOL_REPLY_TO_NOTIFICATION,
            description = "Feature 4: Sends an instant zero-interruption background reply to an active WhatsApp, Telegram, Instagram, or SMS notification without opening the app, or reads the latest replyable notifications.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("sender_or_app", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Sender name or app name to reply to (e.g. 'Rahul', 'Mummy', 'WhatsApp', or 'latest').")
                    })
                    put("reply_text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "The reply message text to send in the background.")
                    })
                })
                put("required", JSONArray().put("reply_text"))
            }
        ),
        ToolDefinition(
            name = TOOL_FILL_SCREEN_FORM,
            description = "Feature 5: Conversational Form-Filling & Smart Field Input. Fills a specific labeled input box on the current screen (e.g. field_label='Name' with value='Sagar Kumar', or field_label='Age' with value='21') without overwriting other fields, or scans and auto-fills the entire form from saved user memory.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("field_label", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Target form field label or hint, e.g. 'Name', 'Age', 'Email', 'Phone', 'Address', or empty to auto-fill all fields from memory.")
                    })
                    put("field_value", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Value to fill into the specified field, e.g. 'Sagar Kumar', '21'.")
                    })
                    put("submit_after", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "True if the user also asked to submit the form after filling.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_CROSS_APP_SCREEN_BRIDGE,
            description = "Feature 6: Reads all text on the current screen, translates or summarizes it into Hindi/Hinglish/English, extracts phone numbers/links/amounts into the clipboard scratchpad, and optionally shares it to WhatsApp or Google Maps.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("target_language", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Language for translation or summary: 'Hindi', 'Hinglish', or 'English'.")
                    })
                    put("route_to_app", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional destination app to forward the extracted text to: 'whatsapp' or 'maps'.")
                    })
                    put("whatsapp_recipient", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional WhatsApp recipient name/number if forwarding to WhatsApp.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_MANAGE_LEARNING_MODE,
            description = "Feature 7: Controls the Teachable 'Watch Me & Learn' Macro Recorder. Starts recording the user's screen clicks/scrolls ('learning mode start karo', 'watch me and learn') or stops and saves the learned skill under a given name ('isko Telegram hi message naam se save kar do').",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Action: 'START_RECORDING' to turn on Learning Mode, or 'STOP_AND_SAVE' to save the recorded demonstration skill.")
                    })
                    put("skill_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Name to save the learned skill as when action is 'STOP_AND_SAVE' (e.g. 'Telegram hi message').")
                    })
                })
                put("required", JSONArray().put("action"))
            }
        ),
        ToolDefinition(
            name = TOOL_SET_ALARM_OR_TIMER,
            description = "Feature 8: Sets an Android system Alarm or countdown Timer via the Clock app without needing manual clicks.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("type", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "'ALARM' or 'TIMER'.")
                    })
                    put("hour", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Hour (0-23) when setting an ALARM.")
                    })
                    put("minutes", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Minutes (0-59) when setting an ALARM.")
                    })
                    put("duration_seconds", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Duration in seconds when setting a TIMER (e.g. 300 for 5 minutes).")
                    })
                    put("label", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional label for the alarm or timer.")
                    })
                })
                put("required", JSONArray().put("type"))
            }
        ),
        ToolDefinition(
            name = TOOL_DEEP_LINK_TELEPORT,
            description = "Feature 9: Zero-Click Deep-Link Teleport. Starts direct turn-by-turn Google Maps navigation to any city/place ('raste ka navigation on karo Patna ke liye', 'navigate to Delhi'), opens YouTube Shorts, plays music from search, or opens UPI QR scanner.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("destination_type", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Deep-link type: 'MAPS_NAVIGATION', 'YOUTUBE_SHORTS', 'YOUTUBE_SEARCH', 'PLAY_MUSIC_SEARCH', or 'UPI_SCANNER'.")
                    })
                    put("query_or_destination", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Clean destination city/place name for MAPS_NAVIGATION (e.g. 'Patna', 'Mumbai', 'Airport') or song/video query.")
                    })
                })
                put("required", JSONArray().put("destination_type"))
            }
        ),
        ToolDefinition(
            name = TOOL_VOICE_UNDO_OR_STOP,
            description = "Feature 10: Agentic Safety Gatekeeper & Instant Voice Undo. Halts any running autonomous loop and navigates back ('Jarvis ruko', 'stop', 'undo karo', 'wapas jao'), or confirms/cancels a pending high-risk safety gate.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "'STOP_AND_UNDO', 'CONFIRM_HIGH_RISK', or 'CANCEL_HIGH_RISK'.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_EXECUTE_TERMUX_COMMAND,
            description = "Executes a Linux bash command or script inside the Termux app environment and returns STDOUT/STDERR output.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("command", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Linux bash command string to execute in Termux (e.g. 'ping -c 3 8.8.8.8', 'python myscript.py', 'ls -la', 'uname -a').")
                    })
                    put("background", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "Whether to execute silently in the background (default true).")
                    })
                })
                put("required", JSONArray().put("command"))
            }
        ),
        ToolDefinition(
            name = TOOL_ANSWER_OR_END_CALL,
            description = "Answers or rejects an incoming ringing phone call and manages speakerphone ('call utha lo', 'phone cut kar do', 'answer call').",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "'ANSWER' to pick up the call, or 'REJECT' to decline/end call.")
                    })
                    put("turn_on_speaker", JSONObject().apply {
                        put("type", "BOOLEAN")
                        put("description", "Whether to turn speakerphone ON when answering the call (default true).")
                    })
                })
                put("required", JSONArray().put("action"))
            }
        ),
        ToolDefinition(
            name = TOOL_TRIGGER_WATCHMAN_ALERT,
            description = "Captures a silent front-camera snapshot of an intruder or stranger and dispatches it via Telegram Bot API or local high-priority security alert.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("reason", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Reason for security alert (e.g. 'wrong PIN attempt', 'unauthorised touch').")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_REPLY_TO_VOICE_NOTE,
            description = "Translates user's spoken Hindi/English response into sender's language and replies directly to an incoming WhatsApp or Telegram voice note.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("chat_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Target sender or group name.")
                    })
                    put("reply_text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Message text to reply with.")
                    })
                })
                put("required", JSONArray().put("reply_text"))
            }
        ),
        ToolDefinition(
            name = TOOL_EXECUTE_BUILTIN_MACRO,
            description = "Executes pre-tested multi-app automation macro (e.g. 'chatgpt image', 'gemini image', 'youtube play song', 'chatgpt se poochho') with screen text waiting and parameter injection.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("macro_name", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Name of the builtin macro, e.g. 'chatgpt image', 'gemini image', 'youtube play song', 'chatgpt se poochho'.")
                    })
                    put("parameters", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "JSON dictionary string of parameters, e.g. '{\"song_query\": \"Arijit Singh song\"}', '{\"image_prompt\": \"cyberpunk cat\"}'.")
                    })
                })
                put("required", JSONArray().put("macro_name"))
            }
        ),
        ToolDefinition(
            name = TOOL_OPEN_IN_APP_BROWSER,
            description = "Opens a website or article URL inside the JARVIS In-App Secure Browser with page text extraction and voice narration.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("url", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Full web URL to open, e.g. 'https://en.wikipedia.org/wiki/Artificial_intelligence'.")
                    })
                })
                put("required", JSONArray().put("url"))
            }
        ),
        ToolDefinition(
            name = TOOL_INSPECT_VISUAL_SCREEN,
            description = "Captures a live visual screenshot of the current Android screen and performs multimodal vision analysis to inspect photos, images, video players/thumbnails, posters, memes, charts, and non-text visual content. Use this whenever the user asks 'screen me kya hai dekho', 'ye photo kiski hai', 'kya dikh raha hai screen par', or to analyze any visual image on the phone.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("query", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Specific visual query or question about the screen (e.g. 'Describe what is visually on screen including photos and videos', 'Analyze the photo/image').")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_AUTO_FILL_OTP,
            description = "Extracts the latest harvested 4-8 digit OTP or verification code from incoming SMS/WhatsApp/Email notifications and automatically injects it into single-box or split-box OTP fields on the active screen.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("explicit_otp", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional explicit OTP digits if specified by the user; otherwise uses the latest harvested OTP from notifications.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_COMPARE_PRICES,
            description = "Autonomous Cross-App Price Comparison & Deal Hunter Agent. Compares product prices across shopping apps (e.g. Amazon vs Flipkart) and reports where the product is cheaper.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("product_query", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Product name to compare prices for, e.g. 'iPhone 15 128GB', 'Sony WH-1000XM5'.")
                    })
                })
                put("required", JSONArray().put("product_query"))
            }
        ),
        ToolDefinition(
            name = TOOL_SCROLL_STITCH_READER,
            description = "Autonomous Long-Page Scroll-Stitching Reader. Automatically scrolls down a long article, PDF, chat history, or Terms & Conditions page multiple times, deduplicates overlapping lines, and summarizes the full document.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("max_scrolls", JSONObject().apply {
                        put("type", "INTEGER")
                        put("description", "Maximum number of downward scroll viewports to stitch (1 to 6, default 3).")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_REGISTER_SENTINEL_RULE,
            description = "Registers an Event-Driven Background Sentinel Rule ('When-X-Happens-Do-Y') that monitors incoming notifications for a sender/keyword and automatically replies or alerts.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("sender_or_app_filter", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Sender name or app name to watch (e.g. 'Boss', 'Rahul', 'Swiggy', 'WhatsApp').")
                    })
                    put("keyword_filter", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional keyword to match inside the notification text (e.g. 'urgent', 'meeting', 'OTP').")
                    })
                    put("auto_reply_text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional automatic reply message to send when the rule triggers.")
                    })
                })
                put("required", JSONArray().put("sender_or_app_filter"))
            }
        ),
        ToolDefinition(
            name = TOOL_WEB_DOM_ACTION,
            description = "Executes an Autonomous In-App Web DOM Agent action (INSPECT, CLICK, or FILL) inside the JARVIS WebView Browser via JavaScript DOM injection.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("action_type", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "'INSPECT', 'CLICK', or 'FILL'.")
                    })
                    put("target_selector_or_text", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Button text, input placeholder, name, id, or CSS selector to click or fill.")
                    })
                    put("value_to_fill", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Text value to fill when action_type is 'FILL'.")
                    })
                })
                put("required", JSONArray().put("action_type"))
            }
        ),
        ToolDefinition(
            name = TOOL_SCREEN_CALL_FOLLOWUP,
            description = "Rejects/screens an incoming phone call and automatically sends a polite WhatsApp/SMS follow-up message to the caller.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("caller_name_or_number", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional caller name or number.")
                    })
                    put("follow_up_message", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Follow-up message text to send after rejecting the call.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_DISPATCH_ANTIGRAVITY_AGENT,
            description = "Dispatches a high-level autonomous task to the Antigravity (agy) agent engine. Use this when the user asks for complex coding, file creation/editing, multi-file codebases, YouTube/Instagram viral SEO generation, video scriptwriting, AutoEditor video rendering, or headless browser automation.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("task_prompt", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Detailed instruction or goal for the Antigravity agent.")
                    })
                    put("subagent_role", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional specialized subagent persona, e.g. 'YouTube SEO Specialist', 'AutoEditor Video Producer', 'Fullstack Developer', 'Web Researcher'.")
                    })
                    put("working_directory", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional workspace directory path.")
                    })
                })
                put("required", JSONArray().put("task_prompt"))
            }
        ),
        ToolDefinition(
            name = TOOL_GET_ANTIGRAVITY_STATUS,
            description = "Checks the live status, running tasks, subagents, or latest step output of the Antigravity autonomous agent.",
            riskLevel = ToolRiskLevel.READ_ONLY,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("conversation_id", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional conversation or task ID to query.")
                    })
                })
            }
        ),
        ToolDefinition(
            name = TOOL_RUN_NATIVE_LINUX_COMMAND,
            description = "Executes a native Linux command directly inside the embedded Termux environment with high-speed PTY, accessing python3, nodejs, git, ffmpeg, and pkg tools.",
            riskLevel = ToolRiskLevel.LOW_RISK,
            parameterSchema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("command", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Linux bash command string to execute.")
                    })
                    put("working_dir", JSONObject().apply {
                        put("type", "STRING")
                        put("description", "Optional working directory.")
                    })
                })
                put("required", JSONArray().put("command"))
            }
        )
    )

    fun buildDeclarationsJsonArray(): JSONArray {
        val array = JSONArray()
        for (tool in ALL_TOOLS) {
            array.put(
                JSONObject().apply {
                    put("name", tool.name)
                    put("description", tool.description)
                    put("parameters", tool.parameterSchema)
                }
            )
        }
        return array
    }
}
