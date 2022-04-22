import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("kotlin-parcelize")
  id("dagger.hilt.android.plugin")
}

repositories {
  mavenCentral()
  google()
}

android {
  compileSdk = 32
  buildToolsVersion = "32.0.0"

  defaultConfig {
    applicationId = "com.wardellbagby.workflow_template"
    minSdk = 21
    targetSdk = 32
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = Dependencies.kotlinComposeCompilerVersion
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

  implementation(Dependencies.KotlinStdlib)
  implementation(Dependencies.RecyclerView)
  implementation(Dependencies.Appcompat)
  implementation(Dependencies.ConstraintLayout)
  implementation(Dependencies.WorkflowUiCoreAndroid)
  implementation(Dependencies.WorkflowTracing)
  implementation(Dependencies.WorkflowUiCompose)
  implementation(Dependencies.Cycler)
  implementation(Dependencies.LifecycleViewModel)
  implementation(Dependencies.ActivityKtx)
  implementation(Dependencies.MaterialViews)
  implementation(Dependencies.MaterialCompose)
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs =
      freeCompilerArgs + "-opt-in=com.squareup.workflow1.ui.WorkflowUiExperimentalApi"
  }
}
