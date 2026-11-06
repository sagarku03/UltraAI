# ⚡ UltraAI: The Ultimate Hybrid Android AI Workstation

> **Next-Gen Android Autonomous Agent + Jarvis Iron Man HUD + Antigravity Engine + Native Termux Linux Runtime**

UltraAI merges the multimodal visual-physical mobile autonomy of **Jarvis AI** with the developer-grade power of **Google Antigravity (`agy`)** and **AntiGem's Native Termux Linux Subsystem**.

---

## 🌟 Key Features

### 1. 🦾 Dual-Engine Agentic Control
* **Jarvis Mobile Controller (`JarvisAccessibilityService`):**
  * Reads screen hierarchy and executes touches, scrolls, and typing in ANY installed Android app (YouTube, WhatsApp, Instagram, Settings).
  * 0% misclick protection, visual quiescence stabilization, and multimodal screen perception.
* **Antigravity Coding & Automation Engine (`agy`):**
  * Autonomous coding, multi-file editing, subagent delegation, and background task execution.
  * YouTube Viral SEO Generation (high-CTR titles, timestamps, keywords, tags).
  * AutoEditor 1080p video rendering and B-roll audio mixing.
  * Headless Browser Automation MCP for scraping and web tasks.

### 2. 🎙️ Real-time Gemini Live Voice API
* Futuristic Iron Man **Arc Reactor HUD** with real-time audio streaming.
* Instant voice-controlled tool execution with natural Hinglish & English responses.
* 5-Tier Cognitive Memory Vault so UltraAI remembers your instructions across sessions.

### 3. 🐧 Built-in Native Termux Linux Runtime (`com.termux`)
* Runs directly with `applicationId = "com.termux"` at 100% native execution speed.
* Full access to `pkg`, `apt`, Python 3, Node.js, Git, FFmpeg, and bash tools inside the app.
* Interactive Glowing Cyber Terminal Console in the HUD.

---

## 📱 Build Variants

| Variant | Package Name | Description |
| :--- | :--- | :--- |
| **`termux`** (Recommended) | `com.termux` | **Self-Contained Linux:** Embeds full Termux bootstrap and environment with zero external dependencies. |
| **`standard`** | `com.jarvisai.assistant` | Standard assistant variant connecting over local gRPC bridge. |
| **`termuxShared`** | `com.termux` | Shared UID variant for external Termux:API companion apps. |

---

## 🚀 Building from Source

```bash
# Clone the repository
git clone https://github.com/sagarku03/UltraAI.git
cd UltraAI

# Build the self-contained Termux APK
./gradlew :app:assembleTermuxDebug

# Or build all debug variants
./gradlew :app:assembleTermuxDebug :app:assembleStandardDebug
```

APKs will be generated under `app/build/outputs/apk/`.

---

## 📄 License

GPL v3.0 / Open Source. Created by Sagar Kurmi & Antigravity.
