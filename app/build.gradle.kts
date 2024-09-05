import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("kotlin-parcelize")
  id("dagger.hilt.android.plugin")
  id("org.jetbrains.kotlin.plugin.compose") version Dependencies.kotlinVersion
}

repositories {
  mavenCentral()
  google()
}

kotlin {
  jvmToolchain(11)
}

android {
  compileSdk = 35
  buildToolsVersion = "35.0.0"
  namespace = "com.wardellbagby.workflow_template"

  defaultConfig {
    applicationId = "com.wardellbagby.workflow_template"
    minSdk = 21
    targetSdk = 35
  }

  buildFeatures {
    compose = true
    viewBinding = true
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android.txt"))
    }
  }
}

dependencies {
  kapt(Dependencies.HiltCompiler)
  implementation(Dependencies.HiltAndroid)

  implementation(Dependencies.Appcompat)
  implementation(Dependencies.WorkflowUiCoreAndroid)
  implementation(Dependencies.WorkflowTracing)
  implementation(Dependencies.WorkflowUiCompose)
  implementation(Dependencies.LifecycleViewModel)
  implementation(Dependencies.ActivityKtx)
  implementation(Dependencies.ActivityCompose)
  implementation(Dependencies.MaterialViews)
  implementation(Dependencies.MaterialCompose)
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions {
    optIn = listOf("com.squareup.workflow1.ui.WorkflowUiExperimentalApi")
  }
}
