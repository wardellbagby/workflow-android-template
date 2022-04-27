// Android Studio doesn't know that changes in this file changes dependencies, so make sure to do
// a manual Gradle sync after changing something here!
object Dependencies {
  const val kotlinComposeCompilerVersion = "1.1.1"

  private const val kotlinVersion = "1.6.10"
  private const val ktlintGradleVersion = "10.2.1"
  private const val agpVersion = "7.1.2"
  private const val hiltVersion = "2.38.1"

  // AndroidX
  private const val recyclerViewVersion = "1.2.1"
  private const val appcompatVersion = "1.4.1"
  private const val activityKtxVersion = "1.4.0"
  private const val materialViewsVersion = "1.5.0"
  private const val materialComposeVersion = "1.0.0-alpha10"
  private const val lifecycleViewModelVersion = "2.4.1"
  private const val constraintLayoutVersion = "2.1.3"

  private const val workflowVersion = "1.7.1"
  private const val cyclerVersion = "0.1.4"

  const val AndroidGradlePlugin = "com.android.tools.build:gradle:$agpVersion"
  const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
  const val KtlintGradlePlugin = "org.jlleitschuh.gradle:ktlint-gradle:$ktlintGradleVersion"

  const val KotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"

  const val HiltCompiler = "com.google.dagger:hilt-compiler:$hiltVersion"
  const val HiltAndroid = "com.google.dagger:hilt-android:$hiltVersion"
  const val HiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"

  const val RecyclerView = "androidx.recyclerview:recyclerview:$recyclerViewVersion"
  const val Appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
  const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"
  const val LifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleViewModelVersion"
  const val ActivityKtx = "androidx.activity:activity-ktx:$activityKtxVersion"
  const val MaterialViews = "com.google.android.material:material:$materialViewsVersion"
  const val MaterialCompose = "androidx.compose.material3:material3:$materialComposeVersion"

  const val WorkflowUiCoreAndroid = "com.squareup.workflow1:workflow-ui-core-android:$workflowVersion"
  const val WorkflowTracing = "com.squareup.workflow1:workflow-tracing:$workflowVersion"
  const val WorkflowUiCompose = "com.squareup.workflow1:workflow-ui-compose:$workflowVersion"

  const val Cycler = "com.squareup.cycler:cycler:$cyclerVersion"
}