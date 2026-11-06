import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.wire)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.jarvisai.assistant"
    minSdk = 24
    targetSdk = 36
    versionCode = 30000
    versionName = "3.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  flavorDimensions += "mode"
  productFlavors {
    create("standard") {
      dimension = "mode"
      applicationId = "com.jarvisai.assistant"
      buildConfigField("int", "BRIDGE_PORT", "1236")
      buildConfigField("int", "HUB_PORT", "1237")
      buildConfigField("int", "BROWSER_MCP_PORT", "8767")
      buildConfigField("boolean", "IS_SHARED_USER_ID", "false")
    }
    create("termux") {
      dimension = "mode"
      applicationId = "com.termux"
      buildConfigField("int", "BRIDGE_PORT", "1234")
      buildConfigField("int", "HUB_PORT", "1235")
      buildConfigField("int", "BROWSER_MCP_PORT", "8765")
      buildConfigField("boolean", "IS_SHARED_USER_ID", "false")
    }
    create("termuxShared") {
      dimension = "mode"
      applicationId = "com.termux"
      buildConfigField("int", "BRIDGE_PORT", "1234")
      buildConfigField("int", "HUB_PORT", "1235")
      buildConfigField("int", "BROWSER_MCP_PORT", "8765")
      buildConfigField("boolean", "IS_SHARED_USER_ID", "true")
    }
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
      all {
        val localJarDir = file("/home/appuser/.m2/repository/org/robolectric/android-all-instrumented/16-robolectric-13921718-i7")
        if (localJarDir.exists()) {
          it.systemProperty("robolectric.offline", "true")
          it.systemProperty("robolectric.dependency.dir", localJarDir.absolutePath)
        }
      }
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  // implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  // implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  // implementation(libs.firebase.appcheck.recaptcha)
  // implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  // implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  // implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  // "ksp"(libs.moshi.kotlin.codegen)

  // Termux Terminal Emulator with Native PTY Support
  implementation("com.github.termux.termux-app:terminal-emulator:v0.118.1")
  implementation("com.google.guava:listenablefuture:1.0")

  // Wire Protobuf & gRPC runtime for Antigravity Agent
  implementation(libs.wire.runtime)
  implementation(libs.wire.grpc.client)
  implementation(libs.kotlinx.serialization.json)

  // Engine utilities & compression (LocalEnvironmentManager & Browser MCP)
  implementation("org.apache.commons:commons-compress:1.26.1")
  implementation("org.tukaani:xz:1.9")
  implementation("org.jsoup:jsoup:1.18.3")
  implementation("androidx.webkit:webkit:1.12.1")
  implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
  implementation(libs.coil.compose)
}

configurations.all {
  resolutionStrategy {
    force("com.squareup.okhttp3:okhttp:4.12.0")
    force("com.squareup.okhttp3:okhttp-android:4.12.0")
    force("com.squareup.okhttp3:logging-interceptor:4.12.0")
  }
}

tasks.withType<com.android.build.gradle.internal.tasks.CheckAarMetadataTask>().configureEach {
  enabled = false
}

wire {
  sourcePath {
    srcDir("src/main/proto")
  }
  kotlin {
    rpcRole = "client"
    rpcCallStyle = "suspending"
  }
}

