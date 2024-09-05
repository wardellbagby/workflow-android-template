// Android Studio doesn't know that changes in this file changes dependencies, so make sure to do
// a manual Gradle sync after changing something here!
object Dependencies {
  const val kotlinVersion = "2.0.20"
  private const val ktlintGradleVersion = "11.1.0"
  private const val agpVersion = "8.6.0"
  private const val hiltVersion = "2.52"

  // AndroidX
  private const val appcompatVersion = "1.6.0"
  private const val activityKtxVersion = "1.6.1"
  private const val materialViewsVersion = "1.8.0"
  private const val materialComposeVersion = "1.0.1"
  private const val lifecycleViewModelVersion = "2.5.1"

  private const val workflowVersion = "1.12.1-beta12"

  const val AndroidGradlePlugin = "com.android.tools.build:gradle:$agpVersion"
  const val KotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
  const val KtlintGradlePlugin = "org.jlleitschuh.gradle:ktlint-gradle:$ktlintGradleVersion"

  const val HiltCompiler = "com.google.dagger:hilt-compiler:$hiltVersion"
  const val HiltAndroid = "com.google.dagger:hilt-android:$hiltVersion"
  const val HiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"

  const val Appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
  const val LifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleViewModelVersion"
  const val ActivityKtx = "androidx.activity:activity-ktx:$activityKtxVersion"
  const val ActivityCompose = "androidx.activity:activity-compose:$activityKtxVersion"
  const val MaterialViews = "com.google.android.material:material:$materialViewsVersion"
  const val MaterialCompose = "androidx.compose.material3:material3:$materialComposeVersion"

  const val WorkflowUiCoreAndroid = "com.squareup.workflow1:workflow-ui-core-android:$workflowVersion"
  const val WorkflowTracing = "com.squareup.workflow1:workflow-tracing:$workflowVersion"
  const val WorkflowUiCompose = "com.squareup.workflow1:workflow-ui-compose:$workflowVersion"
}